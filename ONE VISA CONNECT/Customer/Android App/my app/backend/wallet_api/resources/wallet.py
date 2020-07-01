import os
from flask import request
from flask_restful import Resource

from cryptography.fernet import Fernet
from schemas.wallet import WalletSchema
from models.wallet import WalletModel
from libs.strings import gettext
from libs.encryption import Encryption
from libs.decryption import Decryption

wallet_schema = WalletSchema()


class Wallets(Resource):
    @classmethod
    def get(cls):
        return {"wallets": [wallet_schema.dump(wallet) for wallet in WalletModel.query.all()]}, 200


class Wallet(Resource):
    @classmethod
    def get(cls):
        """
        Checks whether wallet exists or not. If it exists Then checks whether Kyc is done or not.
        :param mobile_number:
        :return:
        """
        data = request.get_json()
        # data = Decyption.decrypt(request.get_json())
        mobile_number = data["mobile_number"]
        wallet = WalletModel.find_by_mobile_number(mobile_number)
        if wallet is None:
            return Encryption.encrypt(
                {"message": gettext("WALLET_NOT_FOUND").format(mobile_number)}
            ), 404

        if not wallet.kyc_status:
            return Encryption.encrypt(
                {"message": gettext("KYC_NOT_DONE").format(mobile_number)}
            ), 404

        return Encryption.encrypt(
            wallet_schema.dump(wallet)
        ), 200

    @classmethod
    def post(cls):
        """
        This endpoint is mainly for Testing purposes...
        payload = {"mobile_number = "*****", "amount" = 8090 , "kyc_status" : true/false}
        """
        data = request.get_json()
        # data = Decyption.decrypt(request.get_json())
        wallet = WalletModel.find_by_mobile_number(data["mobile_number"])
        if wallet:
            return {"message": gettext("WALLET_EXIST").format(data["mobile_number"])}, 400
        else:
            wallet = wallet_schema.load(data)
        try:
            wallet.save_to_db()
        except:
            return {"message": gettext("ERROR_IN_SAVING_WALLET").format(data["mobile_number"])}, 500
        return wallet_schema.dump(wallet), 201


class WalletAmountPay(Resource):
    @classmethod
    def put(cls):
        """
        payload = {"mobile_number = "*****", "amount" = "***"}
        :return:
        """
        data = request.get_json()
        # data = Decyption.decrypt(request.get_json())
        wallet = WalletModel.find_by_mobile_number(data["mobile_number"])
        if wallet is None:
            return Encryption.encrypt(
                {"message": gettext("WALLET_NOT_FOUND").format(data["mobile_number"])}
            ), 404

        if not wallet.kyc_status:
            return Encryption.encrypt(
                {"message": gettext("KYC_NOT_DONE").format(data["mobile_number"])}
            ), 404

        if wallet.amount < data["amount"]:
            return Encryption.encrypt(
                {"message": gettext("NOT_ENOUGH_BALANCE").format(data["mobile_number"])}
            ), 404

        wallet.reduce_amount(data["amount"])
        try:
            wallet.save_to_db()
        except:
            return Encryption.encrypt(
                {"message": gettext("ERROR_IN_SENDING_MONEY")}
            ), 500
        return Encryption.encrypt(
            {"message": gettext("PAYMENT_SUCCESSFUL")}
        ), 200


class WalletAmountAdd(Resource):
    """
    For adding money in an existing wallet
    payload = {"mobile_number": "********", "amount":13131}
    """
    @classmethod
    def put(cls):
        data = request.get_json()
        # data = Decyption.decrypt(request.get_json())
        wallet = WalletModel.find_by_mobile_number(data["mobile_number"])
        if wallet is None:
            return Encryption.encrypt(
                {"message": gettext("WALLET_NOT_FOUND").format(data[mobile_number])}
            ), 400
        try:
            wallet.add_amount(data["amount"])
            wallet.save_to_db()
        except:
            return Encryption.encrypt(
                {"message": gettext("ERROR_IN_ADDING_MONEY")}
            ), 500
        return Encryption.encrypt(
            {"message": gettext("MONEY_ADDED_SUCCESSFULLY")}
        ), 200
        

