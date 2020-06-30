import traceback
import uuid
import time
from datetime import datetime
from pprint import pprint

from flask import request
from flask_restful import Resource

from models.merchant import MerchantModel
from schemas.merchant import MerchantSchema
from flask_jwt_extended import (
    create_access_token,
    create_refresh_token,
    jwt_refresh_token_required,
    get_jwt_identity,
    jwt_required,
    get_raw_jwt
)
from models.history import HistoryModel
from visaApi.pullFunds import FundsTransfer
from blacklist import BLACKLIST
from uniqueIds import SystemsTraceAuditNumber, TerminalId, IdCode
from authApi.visaNet import VisaNet
from libs.retrievalNo import RetrievalNo
from libs.countryCode import countryCdoe

merchant_schema = MerchantSchema()

# constants
MERCHANT_NOT_FOUND = "{} not found"
MERCHANT_ALREADY_EXISTS = "{} already exists"
MERCHANT_CREATED = "MERCHANT: {} created successfully, HURRAY!!"
MERCHANT_DELETED = "MERCHANT: {} deleted successfully"
INVALID_PASSWORD = "Invalid Password"
MERCHANT_LOGGED_OUT = "MERCHANT logged out successfully"
MERCHANT_NOT_CONFIRMED = "{}"
MERCHANT_CONFIRMED = "{}"
OTP_SENT = "OTP has been sent to {}"
OTP_FAILED = "Couldn't send OTP"
INTERNAL_SERVER_ERROR = "Internal Server Error! Try Again"
PAYMENT_CANNOT_BE_COMPLETED = "Payment Can not Be Completed Due To Insufficient Balance"


class MerchantRegister(Resource):
    @classmethod
    def post(cls):
        merchant = merchant_schema.load(request.get_json())
        if MerchantModel.find_merchant_by_email(merchant.email):
            return {"msg": MERCHANT_ALREADY_EXISTS.format(merchant.email)}, 400
        if MerchantModel.find_merchant_by_mobile_number(merchant.mobile_number):
            return {"msg": MERCHANT_ALREADY_EXISTS.format(merchant.mobile_number)}, 400
        merchant.acquirerCountryCode = countryCdoe[merchant.country]
        terminalId = str(uuid.uuid4().int >> 32)[0:8]
        while terminalId in TerminalId:
            terminalId = str(uuid.uuid4().int >> 32)[0:8]
        idCode = str(uuid.uuid4().int >> 32)[0:15]
        while idCode in IdCode:
            idCode = str(uuid.uuid4().int >> 32)[0:15]
        try:
            merchant.terminalId = terminalId
            merchant.idCode = idCode
            TerminalId.add(terminalId)
            IdCode.add(idCode)
            merchant.save_to_db()
        except:
            return {"msg": INTERNAL_SERVER_ERROR}, 500
        return {"msg": MERCHANT_CREATED.format(merchant.email)}, 201


class MerchantLogin(Resource):
    @classmethod
    def post(cls):
        json_data = request.get_json()
        merchant_email = json_data["email"]
        merchant_password = json_data["password"]
        merchant = MerchantModel.find_merchant_by_email(email=merchant_email)
        if not merchant:
            return {"msg": MERCHANT_NOT_FOUND.format(merchant_email)}, 401
        elif merchant.password != merchant_password:
            return {"msg": INVALID_PASSWORD}, 401
        # elif not merchant.activated:
        #     return {"msg": MERCHANT_NOT_CONFIRMED.format(merchant.mobile_number)}, 400
        
        access_token = create_access_token(identity=merchant.id, fresh=True)
        refresh_token = create_refresh_token(identity=merchant.id)
        return {"access_token": access_token, "refresh_token": refresh_token,
                "merchant": merchant_schema.dump(merchant)}, 200


class Merchant(Resource):
    @classmethod
    @jwt_required
    def get(cls):  # get using phone number (can be changed per use case)
        _id = get_jwt_identity()
        merchant = MerchantModel.find_merchant_by_id(_id)
        if not merchant:
            return {"msg": MERCHANT_NOT_FOUND}, 404
        return merchant_schema.dump(merchant), 200

    # # just for testing
    # @classmethod
    # def delete(cls, mobile_number):
    #     merchant = MerchantModel.find_merchant_by_mobile_number(mobile_number=mobile_number)
    #     if not merchant:
    #         return {"msg": MERCHANT_NOT_FOUND.format(mobile_number)}, 404
    # 
    #     merchant.delete_from_db()
    #     return {"msg": MERCHANT_DELETED.format(merchant.email)}, 200


class MerchantLogout(Resource):
    @classmethod
    @jwt_required
    def post(cls):
        jti = get_raw_jwt()["jti"]
        BLACKLIST.add(jti)
        return {"msg": MERCHANT_LOGGED_OUT}, 200


class TokenRefresh(Resource):
    @classmethod
    @jwt_refresh_token_required
    def post(cls):
        merchant_id = get_jwt_identity()
        new_access_token = create_access_token(identity=merchant_id, fresh=True)  # creating new fresh token
        return {"access_token": new_access_token}, 200


class ReceivePayment(Resource):
    @classmethod
    @jwt_required
    def post(cls):
        _id = get_jwt_identity()
        payload = request.get_json()

        merchant = MerchantModel.find_merchant_by_id(_id)
        if merchant is None:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        payload["acquirerCountryCode"] = merchant.acquirerCountryCode
        payload["acquiringBin"] = merchant.acquiringBin
        payload["businessApplicationId"] = merchant.businessApplicationId
        payload["cardAcceptor"] = {
            "address": {
                "country": merchant.country,
                "state": merchant.state,
                "zipCode": merchant.zipCode
            },
            "idCode": merchant.idCode,
            "name": merchant.name,
            "terminalId": merchant.terminalId
        }

        systemsTraceAuditNumber = str(uuid.uuid4().int >> 32)[0:6]
        while systemsTraceAuditNumber in SystemsTraceAuditNumber:
            systemsTraceAuditNumber = str(uuid.uuid4().int >> 32)[0:6]
        SystemsTraceAuditNumber.add(systemsTraceAuditNumber)

        payload["systemsTraceAuditNumber"] = systemsTraceAuditNumber
        payload["localTransactionDateTime"] = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S")
        payload["retrievalReferenceNumber"] = RetrievalNo.No() + str(systemsTraceAuditNumber)

        payload["senderPrimaryAccountNumber"] = "4895142232120006"
        # print(payload)

        mobile_number = ""
        wallet_name = ""
        flag = False
        status_code = False
        if "mobile_number" in payload:
            flag = True
            payloadAuthApi = {}
            mobile_number = payload["mobile_number"]
            wallet_name = payload["wallet_name"]
            payloadAuthApi["mobile_number"] = payload["mobile_number"]
            payloadAuthApi["wallet_name"] = payload["wallet_name"]
            del (payload["mobile_number"])
            del (payload["wallet_name"])
            payloadAuthApi["merchant_name"] = merchant.name
            payloadAuthApi["amount"] = payload["amount"]
            payloadAuthApi["systemsTraceAuditNumber"] = systemsTraceAuditNumber
            r = VisaNet.AmountConfirmation(payloadAuthApi)
            print(r)
            print(r.json())
            if r.status_code != 200:
                history = HistoryModel(amount=payload["amount"],
                                       transaction_id=systemsTraceAuditNumber,
                                       transaction_time=payload["localTransactionDateTime"],
                                       merchant_mobile_number=merchant.mobile_number,
                                       customer_mobile_number=mobile_number,
                                       customer_wallet_name=wallet_name,
                                       merchant_name=merchant.name,
                                       status=status_code
                                       )
                history.save_to_db()
                return {'msg': PAYMENT_CANNOT_BE_COMPLETED}, 400
            
        if "wallet_name" in payload:
            del(payload["wallet_name"])
            
        response = FundsTransfer.merchant_pull_payments_post_response(payload)

        if response.status_code != 200:
            if flag:
                payloadAuthApi = {
                    'mobile_number': mobile_number,
                    'pan': payload["senderPrimaryAccountNumber"],
                    'systemsTraceAuditNumber': systemsTraceAuditNumber,
                    'code': response.status_code
                }
                r = VisaNet.TransactionConfirmation(payloadAuthApi)
            history = HistoryModel(amount=payload["amount"],
                                   transaction_id=systemsTraceAuditNumber,
                                   transaction_time=payload["localTransactionDateTime"],
                                   merchant_mobile_number=merchant.mobile_number,
                                   customer_mobile_number=mobile_number,
                                   customer_wallet_name=wallet_name,
                                   merchant_name=merchant.name,
                                   status=status_code
                                   )
            history.save_to_db()
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        if response.status_code == 200:
            if flag:
                payloadAuthApi = {
                    'mobile_number': mobile_number,
                    'pan': payload["senderPrimaryAccountNumber"],
                    'systemsTraceAuditNumber': systemsTraceAuditNumber,
                    'code': response.status_code
                }
                r = VisaNet.TransactionConfirmation(payloadAuthApi)
            status_code = True
        history = HistoryModel(amount=payload["amount"],
                               transaction_id=systemsTraceAuditNumber,
                               transaction_time=payload["localTransactionDateTime"],
                               merchant_mobile_number=merchant.mobile_number,
                               customer_mobile_number=mobile_number,
                               customer_wallet_name=wallet_name,
                               merchant_name=merchant.name,
                               status=status_code
                               )
        history.save_to_db()
        # response = FundsTransfer.merchant_push_payments_post_response()
        return response

# class MerchantConfirm(Resource):
#     @classmethod
#     def post(cls):
#         json_data = request.get_json()
#         merchant_data = merchant_schema.load({"mobile_number": json_data["mobile_number"]},
#                                              partial=("full_name", "email", "password"))
#         otp = json_data["OTP"]
#         merchant = MerchantModel.find_merchant_by_mobile_number(mobile_number=merchant_data.mobile_number)
#         if not merchant:
#             return {"msg": MERCHANT_NOT_FOUND}, 404
#         try:
#             message = merchant.send_otp(otp)
#         except:
#             traceback.print_exc()
#             return {"msg": OTP_FAILED}, 500
#
#         return {"msg": OTP_SENT.format(merchant.mobile_number)}, 200
#
#     @classmethod
#     def put(cls):
#         json_data = request.get_json()
#         merchant_data = merchant_schema.load({"mobile_number": json_data["mobile_number"]}
#                                              , partial=("full_name", "email", "password"))
#         merchant = MerchantModel.find_merchant_by_mobile_number(mobile_number=merchant_data.mobile_number)
#         if not merchant:
#             return {"msg": MERCHANT_NOT_FOUND}, 404
#         merchant.activated = True
#         merchant.save_to_db()
#         return {"msg": MERCHANT_CONFIRMED.format(merchant.mobile_number)}, 200


# {
#   "acquirerCountryCode": "840",
#   "acquiringBin": "408999",
#   "amount" :200,
#   "businessApplicationId": "PP",
#    "cardAcceptor": {
#     "address":{
#                 "country": "IND",
#                 "state": "GUJ",
#                 "zipCode": 132001
#             },
#     "idCode": "BBFDD3463",
#     "name": "Beans",
#     "terminalId": "ABCDsds"
# },
# "localTransactionDateTime": "2020-06-27T13:21:47",
# "retrievalReferenceNumber": "330000550005",
# "senderCardExpiryDate": "2015-10",
# "senderCurrencyCode": "USD",
# "systemsTraceAuditNumber": "451005",
# "senderPrimaryAccountNumber": "4895142232120006"
# }
