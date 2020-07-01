import traceback
from flask_restful import Resource
from flask import request
from flask_jwt_extended import jwt_required
from models.virtualCard import VirtualCardModel
from models.history import HistoryModel
from visa.visaAPI import MVisa
from walletAPI.wallet import Wallet
from libs.security import AESCipher
from libs.decryption import Decryption
from datetime import datetime
import time
from pan import PAN
import uuid

CARD_GENERATED = "CARD IS ALREADY GENERATED"
CARD_NOT_GENERATED = "CARD NOT GENERATED"
INTERNAL_SERVER_ERROR = 'INTERNAL SERVER ERROR'
FAILED_TO_CREATE = 'FAILED TO CREATE'
KYC_STATUS = 'WALLET UNAUTHORIZED CHECK KYC STATUS'
PAN_CREATED = 'PAN CREATED SUCCESSFULLY,HURRAY!!'
AMOUNT_ADDED = 'MONEY ADDED'
ROLL_BACK = "INTERNAL SERVER ERROR, AMOUNT ROLLBACK"
INSUFFICIENT_FUNDS = 'INSUFFICIENT FUNDS'
ACCOUNT_NOT_YET_SYNCED = "ACCOUNT NOT YET SYNCED"
DATABASE_ERROR = "ERROR IN SAVING TO DATABASE, VISA RESPONSE OK"

wallet = Wallet()
visa = MVisa()
cipher = AESCipher('mysecretpassword')


class VirtualCard(Resource):

    @classmethod
    @jwt_required
    def get(cls):
        """
        Accessing already generated card.
        :return:
        """
        payload = request.get_json()
        mobile_number = payload['mobile_number']
        try:
            virtual_card = VirtualCardModel.find_by_mobile_number(mobile_number)
        except:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if not virtual_card:
            return {"msg": ACCOUNT_NOT_YET_SYNCED}, 400

        wallet_response = wallet.authorize(mobile_number)

        if wallet_response is None:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if wallet_response.status_code == 404:
            return Decryption.decrypt(wallet_response.json()), 401

        wallet_response = Decryption.decrypt(wallet_response.json())
        return {"msg": CARD_GENERATED, "wallet_amount": wallet_response['amount']}, 200

    @classmethod
    @jwt_required
    def post(cls):
        """
        It creates PAN details if not yet created while checking your wallet was authorized.
        :return:
        """
        payload = request.get_json()
        mobile_number = payload['mobile_number']
        try:
            virtual_card = VirtualCardModel.find_by_mobile_number(mobile_number)
        except:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if virtual_card:
            return {"msg": CARD_GENERATED}, 400

        wallet_response = wallet.authorize(mobile_number)

        if wallet_response is None:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if wallet_response.status_code == 404:
            return Decryption.decrypt(wallet_response.json()), 401

        pan_pref = '40'
        pan = pan_pref + str(uuid.uuid4().int >> 32)[0:14]
        pan = cipher.encrypt(pan)

        while pan in PAN:
            pan = pan_pref + str(uuid.uuid4().int >> 32)[0:14]
            pan = cipher.encrypt(pan)

        card_generated_time = datetime.fromtimestamp(time.time()).isoformat()
        virtual_card = VirtualCardModel(pan, card_generated_time, mobile_number)

        wallet_response = Decryption.decrypt(wallet_response.json())
        try:
            virtual_card.save_to_db()
            PAN.add(pan)
        except:
            traceback.print_exc()
            return {"msg": FAILED_TO_CREATE}, 500

        return {"msg": PAN_CREATED, "wallet_amount": wallet_response['amount']}, 201


# class AddAmount(Resource):
#
#     @classmethod
#     # @jwt_required
#     def put(cls, mobile_number: str):
#         """
#         Adds amount to your virtual card temporarily.
#         :param mobile_number:
#         :return:
#         """
#
#         payload = request.get_json()
#         virtual_card = VirtualCardModel.find_by_mobile_number(mobile_number)
#         if not virtual_card:
#             return {"message": CARD_NOT_GENERATED}, 400
#
#         wallet_response = wallet.get_amount(mobile_number, payload['amount'])
#
#         if wallet_response is None:
#             return {'message': INTERNAL_SERVER_ERROR}, 500
#
#         if wallet_response.status_code == 400:
#             return {'message': wallet_response.json()}, 400
#
#         virtual_card.amount += payload['amount']
#
#         try:
#             virtual_card.save_to_db()
#         except:
#             return {"message": INTERNAL_SERVER_ERROR}, 500
#
#         return {"message": AMOUNT_ADDED}, 200


class Payment(Resource):

    @classmethod
    @jwt_required
    def put(cls):
        """
        Completes the payment via VISA NET using mVisa API.
        :return:
        payload will have mobile number
        """

        payload = request.get_json()
        mobile_number = payload["mobile_number"]
        wallet_name = payload["wallet_name"]
        del (payload["mobile_number"])
        del (payload["wallet_name"])

        try:
            virtual_card = VirtualCardModel.find_by_mobile_number(mobile_number)
        except:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if not virtual_card:
            return {'msg': CARD_NOT_GENERATED}, 400

        wallet_response = wallet.get_amount(mobile_number, float(payload['amount']))
        pan = cipher.decrypt(virtual_card.pan)

        if wallet_response is None:
            return {'msg': INTERNAL_SERVER_ERROR}, 500

        if wallet_response.status_code == 404:
            return Decryption.decrypt(wallet_response.json()), 401

        systems_trace_audit_number = str(uuid.uuid4().int >> 32)[0:6]
        last_transaction_time = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S")
        payload['senderAccountNumber'] = pan
        payload['systemsTraceAuditNumber'] = systems_trace_audit_number
        payload['retrievalReferenceNumber'] = str(datetime.utcnow().strftime("%y%d%H")) + \
                                              systems_trace_audit_number
        payload['localTransactionDateTime'] = last_transaction_time

        visa_response = MVisa.merchant_push_payments_post_payload(payload)

        if visa_response is None:
            wallet_response = wallet.send_amount(mobile_number, float(payload['amount']))
            return {"msg": ROLL_BACK}, 500

        visa_response_status = visa_response.status_code
        visa_response = visa_response.json()
        if visa_response_status != 200:
            wallet_response = wallet.send_amount(mobile_number, float(payload['amount']))
            return {"msg": ROLL_BACK}, 500

        virtual_card.last_transaction_time = last_transaction_time
        history = HistoryModel(payload['amount'], last_transaction_time, mobile_number,
                               systems_trace_audit_number, payload["cardAcceptor"]["name"], wallet_name,
                               "Success")
        virtual_card.count += 1

        try:
            virtual_card.save_to_db()
            history.save_to_db()
        except:
            return {"msg": DATABASE_ERROR}, 500

        return {'msg': visa_response}, 200
