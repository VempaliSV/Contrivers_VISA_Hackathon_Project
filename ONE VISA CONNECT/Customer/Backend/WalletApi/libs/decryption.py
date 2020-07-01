import os
from typing import Dict
from cryptography.fernet import Fernet


class Decryption:
    @classmethod
    def decrypt(cls, data: Dict) -> Dict:
        """
        This method is used for decryption of response which is received from Auth api...
        :param data: This is response in the form of dict like -> {"msg": "Encrypted data"}
        :return: Return decrypted data like -> {"msg": "Decrypted Data"}
        """
        # Decryption Key Saved in Environment Variable
        key = os.environ.get("WALLET_KEY")
        f = Fernet(key)
        decrypted_data = {}
        for i in data:
            decrypted_data[i] = \
                f.decrypt(bytes(data[i], 'ascii')).decode('ascii')
        # print(decrypted_data)
        return decrypted_data
