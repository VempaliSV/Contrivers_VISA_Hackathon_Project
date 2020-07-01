import os
from typing import Dict
from cryptography.fernet import Fernet
import json


class Encryption:
    @classmethod
    def encrypt(cls, data: Dict):
        print(data, type(data))
        key = open("./libs/wallet_key.key").read()
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
