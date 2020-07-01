import os
from flask import request
from flask_restful import Resource

from schemas.wallet import WalletSchema
from models.wallet import WalletModel
from libs.strings import gettext
from libs.encryption import Encryption
from libs.decryption import Decryption

wallet_schema = WalletSchema()


class Wallet(Resource):
    @classmethod
    def get(cls):
        """
        Checks whether wallet exists or not. If it exists Then checks whether Kyc is True or not.
        :param : data is received in the form of payload like {"mobile_number": "value"}
        :return: encrypted response
            if there is some error in finding wallet or kyc not done then return {"message": "encrypted message"}
            else return wallet data like {"amount": "encrypted value",
                                          "kyc": "Encrypted value",
                                          "mobile_number": "Encrypted Value"
                                          }
        """
        data = request.get_json()
        data = Decryption.decrypt(request.get_json())
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


class WalletAmountPay(Resource):
    @classmethod
    def put(cls):
        """
        This method is mainly used for checking whether enough money is present in the wallet during payment or not.
        If enough amount exists then it reduces the amount for that person in the database
        and return encrypted message.
        payload = {"mobile_number = "*****", "amount" = "***"}
        :return: {"message": "Encrypted message"}
        """
        data = request.get_json()
        data = Decryption.decrypt(request.get_json())
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

    @classmethod
    def put(cls):
        """
            This method is used mainly during rollback to add amount back in the wallet if the transaction fails.
            payload = {"mobile_number": "********", "amount":13131}
            returns a encrypted message.
        """
        data = request.get_json()
        data = Decryption.decrypt(request.get_json())
        wallet = WalletModel.find_by_mobile_number(data["mobile_number"])
        if wallet is None:
            return Encryption.encrypt(
                {"message": gettext("WALLET_NOT_FOUND").format(data["mobile_number"])}
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
