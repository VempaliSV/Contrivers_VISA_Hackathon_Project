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
        """
        Used for registration of merchant..
        payload={
            "name":***********,
            "email":**********,
            "password":*******,
            "mobile_number":**,
            "state":**********,
            "country":********,
            "acquirerCountryCode":****,
            "zipCode": ********
        }
        :return: {"msg": error message or merchant created message}
        """
        merchant = merchant_schema.load(request.get_json())

        if MerchantModel.find_merchant_by_email(merchant.email):
            return {"msg": MERCHANT_ALREADY_EXISTS.format(merchant.email)}, 400

        if MerchantModel.find_merchant_by_mobile_number(merchant.mobile_number):
            return {"msg": MERCHANT_ALREADY_EXISTS.format(merchant.mobile_number)}, 400

        # maps country name to country code present in libs.countryCode file
        merchant.acquirerCountryCode = countryCdoe[merchant.country]

        # Generates a unique TerminalId
        terminalId = str(uuid.uuid4().int >> 32)[0:8]
        while terminalId in TerminalId:
            terminalId = str(uuid.uuid4().int >> 32)[0:8]

        # Generates a unique idCode
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
        """
        payload= {
            "email":*************,
            "password":**********
        }
        :return: Error message if credentials are not valid.
        if they are valid
        return {"access_token":******************, "refresh_token":*********}
        """
        json_data = request.get_json()
        merchant_email = json_data["email"]
        merchant_password = json_data["password"]
        merchant = MerchantModel.find_merchant_by_email(email=merchant_email)
        if not merchant:
            return {"msg": MERCHANT_NOT_FOUND.format(merchant_email)}, 401
        elif merchant.password != merchant_password:
            return {"msg": INVALID_PASSWORD}, 401
        
        access_token = create_access_token(identity=merchant.id, fresh=True)
        refresh_token = create_refresh_token(identity=merchant.id)
        return {"access_token": access_token, "refresh_token": refresh_token,
                "merchant": merchant_schema.dump(merchant)}, 200


class Merchant(Resource):
    @classmethod
    @jwt_required
    def get(cls):
        # finding identity of using access_token
        _id = get_jwt_identity()
        merchant = MerchantModel.find_merchant_by_id(_id)
        if not merchant:
            return {"msg": MERCHANT_NOT_FOUND}, 404
        return merchant_schema.dump(merchant), 200


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
        """
        This method is used when merchant scans a Qr code of customer and send a pull payment request.
        :return: msg according to status of transaction
        """
        _id = get_jwt_identity()
        payload = request.get_json()

        merchant = MerchantModel.find_merchant_by_id(_id)
        if merchant is None:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        # Setting essential fields of Payload for pull funds transfer call
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

        # Generates a unique system trace audit number.
        systemsTraceAuditNumber = str(uuid.uuid4().int >> 32)[0:6]
        while systemsTraceAuditNumber in SystemsTraceAuditNumber:
            systemsTraceAuditNumber = str(uuid.uuid4().int >> 32)[0:6]
        SystemsTraceAuditNumber.add(systemsTraceAuditNumber)

        payload["systemsTraceAuditNumber"] = systemsTraceAuditNumber
        payload["localTransactionDateTime"] = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S")
        payload["retrievalReferenceNumber"] = RetrievalNo.No() + str(systemsTraceAuditNumber)

        payload["senderPrimaryAccountNumber"] = "4895142232120006"

        # customer mobile number present in the qr scanned by merchant
        mobile_number = ""
        # customer wallet name
        wallet_name = ""

        # FLag tells whether we get mobile number from customer details or not.
        flag = False

        # status code is for transaction shows whether the transaction was successful or not
        status_code = False

        if "mobile_number" in payload:

            # mobile_number is present in payload
            flag = True

            # Setting payload for call to AuthApi for Confirmation of amount present in wallet of sender
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

            # call to authApi for confirmation of amount entered by customer.
            r = VisaNet.AmountConfirmation(payloadAuthApi)

            if r.status_code != 200:
                # Updating History for transaction failure.
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

        #    deleting nonessential fields of payload
        if "wallet_name" in payload:
            del(payload["wallet_name"])

        #   Sending pull payments request to helper function
        response = FundsTransfer.merchant_pull_payments_post_response(payload)

        if response.status_code != 200:
            if flag:
                payloadAuthApi = {
                    'mobile_number': mobile_number,
                    'pan': payload["senderPrimaryAccountNumber"],
                    'systemsTraceAuditNumber': systemsTraceAuditNumber,
                    'code': response.status_code
                }
                # Sending request for rollback of payment denoted by code of payload
                r = VisaNet.TransactionConfirmation(payloadAuthApi)

            #   setting history for payment failure
            history = HistoryModel(amount=payload["amount"],
                                   transaction_id=systemsTraceAuditNumber,
                                   transaction_time=payload["localTransactionDateTime"],
                                   merchant_mobile_number=merchant.mobile_number,
                                   customer_mobile_number=mobile_number,
                                   customer_wallet_name=wallet_name,
                                   merchant_name=merchant.name,
                                   status=status_code
                                   )
            # saving history in the database
            history.save_to_db()
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        # payment approved by visa pull funds transfer api
        if response.status_code == 200:
            if flag:
                payloadAuthApi = {
                    'mobile_number': mobile_number,
                    'pan': payload["senderPrimaryAccountNumber"],
                    'systemsTraceAuditNumber': systemsTraceAuditNumber,
                    'code': response.status_code
                }
                # Sending confirmation of transaction to Auth api denoted by code of transaction
                r = VisaNet.TransactionConfirmation(payloadAuthApi)
            status_code = True

        # setting history for payment success
        history = HistoryModel(amount=payload["amount"],
                               transaction_id=systemsTraceAuditNumber,
                               transaction_time=payload["localTransactionDateTime"],
                               merchant_mobile_number=merchant.mobile_number,
                               customer_mobile_number=mobile_number,
                               customer_wallet_name=wallet_name,
                               merchant_name=merchant.name,
                               status=status_code
                               )

        # Saving history in the database.
        history.save_to_db()
        return response

