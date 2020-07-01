from flask import request
from flask_restful import Resource
from flask_jwt_extended import jwt_required
from datetime import datetime

from models.virtualCard import VirtualCardModel
from models.history import HistoryModel
from libs.security import AESCipher
from walletAPI.wallet import Wallet

CARD_GENERATED = "CARD IS ALREADY GENERATED"
CARD_NOT_GENERATED = "CARD NOT GENERATED"
INTERNAL_SERVER_ERROR = 'INTERNAL SERVER ERROR'
FAILED_TO_CREATE = 'FAILED TO CREATE'
KYC_STATUS = 'WALLET UNAUTHORIZED CHECK KYC STATUS'
PAN_CREATED = 'PAN CREATED SUCCESSFULLY,HURRAY!!'
AMOUNT_TRANSFERRING = 'MONEY TRANSFERRING'
AMOUNT_ADDED_BACK = "AMOUNT ADDED BACK TO WALLET"
INSUFFICIENT_FUNDS = 'INSUFFICIENT FUNDS'
ACCOUNT_NOT_YET_SYNCED = "ACCOUNT NOT YET SYNCED"

cipher = AESCipher('mysecretpassword')
wallet = Wallet()


class VisaNet(Resource):

    @classmethod
    @jwt_required
    def get(cls):
        """
        payload = {
        "mobile_number":"*******",
        }
        """
        payload = request.get_json()

        try:
            virtual_card = VirtualCardModel.find_by_mobile_number(payload['mobile_number'])
        except:
            return {"message": INTERNAL_SERVER_ERROR}, 500

        if not virtual_card:
            return {"message": CARD_NOT_GENERATED}, 400

        pan = cipher.decrypt(virtual_card.pan)

        return {"pan": pan}, 200

    @classmethod
    @jwt_required
    def put(cls):
        """
        payload = {
        "mobile_number":"*******",
        "amount":"**",
        "wallet_name":"***",
        "merchant_name":"****",
        "systemsTraceAuditNumber":"*****",
        }

        """

        payload = request.get_json()
        history = None
        # try:
        #     history = HistoryModel.find_by_mobile_status(payload['mobile_number'], "Pending")
        # except Exception as e:
        #     print(e)

        # if history:
        #     for hist in history:
        #         hist.status = "Refunded"
        #         wallet_response = wallet.send_amount(payload['mobile_number'], hist.amount)

        #         if wallet_response.status_code == 200:
        #             try:
        #                 hist.save_to_db()
        #             except Exception as e:
        #                 print(e)

        try:
            virtual_card = VirtualCardModel.find_by_mobile_number(payload["mobile_number"])
        except:
            return {"message": INTERNAL_SERVER_ERROR}, 500

        if not virtual_card:
            return {'message': CARD_NOT_GENERATED}, 400

        wallet_response = wallet.get_amount(payload["mobile_number"], float(payload['amount']))

        if wallet_response is None:
            return {'message': INTERNAL_SERVER_ERROR}, 500

        if wallet_response.status_code == 404:
            return Decryption.decrypt(wallet_response.json()), 400

        last_transaction_time = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S")

        history = HistoryModel(payload['amount'], last_transaction_time, payload["mobile_number"],
                               payload["transaction_id"], payload["merchant_name"], payload['wallet_name'],
                               "Pending")

        try:
            history.save_to_db()
        except:
            return {"message": INTERNAL_SERVER_ERROR}, 500

        return {"message": AMOUNT_TRANSFERRING}, 200


class Confirmation(Resource):

    @classmethod
    def put(cls):
        """
        payload = {
        "mobile_number":"*******",
        "pan":"**********",
        "systemsTraceAuditNumber":"******",
        "code":"*****"  
        }

        """
        payload = request.get_json()

        try:
            history = HistoryModel.find_by_transaction_id(payload['systemsTraceAuditNumber'])
        except:
            return {"message": INTERNAL_SERVER_ERROR}, 500

        if payload['code'] != 200:

            wallet_response = wallet.send_amount(payload['mobile_number'], history.amount)

            try:
                history.save_to_db()
            except:
                return {"message": INTERNAL_SERVER_ERROR}, 500

            if wallet_response.status_code == 200:
                history.status = "Refunded"
                try:
                    history.save_to_db()
                except:
                    return {"message": INTERNAL_SERVER_ERROR}, 500

                return {"message": "Refunded"}, 200

            return Decryption.decrypt(wallet_response.json()), 500

        history.status = "Success"
        history.transaction_id = payload['transaction_id']

        try:
            history.save_to_db()
        except:
            return {"message": INTERNAL_SERVER_ERROR}, 500

        return {"message": "Successful Payment"}, 200
