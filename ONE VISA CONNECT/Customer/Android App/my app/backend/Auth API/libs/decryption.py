import os
from typing import Dict
from cryptography.fernet import Fernet


class Decryption:
    @classmethod
    def decrypt(cls, data: Dict):
        key = open("./libs/wallet_key.key").read()
        f = Fernet(key)
        decrypted_data = {}
        for i in data:
            decrypted_data[i] = \
                f.decrypt(bytes(data[i], 'ascii')).decode('ascii')
        # print(decrypted_data)
        return decrypted_data
