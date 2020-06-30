import os
from typing import Dict
from cryptography.fernet import Fernet
import json


class Encryption:
    @classmethod
    def encrypt(cls, data: Dict) -> Dict:
        """
        This method is used for encryption of response or payload.
        :param data: This is response in the form of dict like -> {"msg": "Encrypted data"}
        :return: Return decrypted data like -> {"msg": "Decrypted Data"}
        """
        key = os.environ.get("wallet_key")
        wallet_key = bytes(key, 'ascii')
        f = Fernet(wallet_key)
        encrypted_data = {}
        a = ""
        for i in data:
            if type(data[i]) != type(a):
                data[i] = str(data[i])
            encrypted_data[i] = \
                f.encrypt(bytes(data[i], 'ascii')).decode('ascii')
        # print(type(encrypted_data),encrypted_data)
        return encrypted_data
