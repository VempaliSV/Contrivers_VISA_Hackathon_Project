import json
import requests
from libs.encryption import Encryption

port = 'Enter the port URL for wallet'

wallet_url = "/wallet"
wallet_url_amount_pay = "/wallet/amount/pay"
wallet_url_amount_receive = "/wallet/amount/receive"

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 10


class Wallet:
    """
    Interacts with Wallet Api.
    """

    @classmethod
    def authorize(cls, mobile_number: str):
        payload = {
            "mobile_number": mobile_number
        }

        payload = json.dumps(Encryption.encrypt(payload))
        url = port + wallet_url

        try:
            response_get = requests.get(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None
        return response_get

    @classmethod
    def get_amount(cls, mobile_number: str, amount: float):
        url = port + wallet_url_amount_pay
        payload = {
            "mobile_number": mobile_number,
            "amount": amount
        }

        payload = json.dumps(Encryption.encrypt(payload))
        try:
            response_amount = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None

        return response_amount

    @classmethod
    def send_amount(cls, mobile_number: str, amount: float):

        url = port + wallet_url_amount_receive
        payload = {
            "mobile_number": mobile_number,
            "amount": amount
        }

        payload = json.dumps(Encryption.encrypt(payload))
        try:
            response_amount = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None

        return response_amount
