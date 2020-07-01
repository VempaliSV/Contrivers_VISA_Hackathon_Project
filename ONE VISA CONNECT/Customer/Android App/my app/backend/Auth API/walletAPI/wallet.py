import json
import requests
from libs.encryption import Encryption

port = 'http://127.0.0.1:5000'
# port = 'https://wallet-api-v1.herokuapp.com'

wallet_url = "/wallet"
wallet_url_amount_pay = "/wallet/amount/pay"
wallet_url_amount_receive = "/wallet/amount/receive"

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 10


class Wallet:

    def authorize(self, mobile_number: str):
        payload = {
            "mobile_number": mobile_number
        }
        # payload = json.dumps(Encryption.encrypt(payload))
        url = port + wallet_url

        try:
            response_get = requests.get(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None
        return response_get

    def get_amount(self, mobile_number: str, amount: float):
        url = port + wallet_url_amount_pay
        payload = {
            "mobile_number": mobile_number,
            "amount": amount
        }
        # payload = json.dumps(Encryption.encrypt(payload))
        try:
            response_amount = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None

        return response_amount

    def send_amount(self, mobile_number: str, amount: float):
        url = port + wallet_url_amount_receive
        payload = {
            "mobile_number": mobile_number,
            "amount": amount
        }
        # payload = json.dumps(Encryption.encrypt(payload))
        try:
            response_amount = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except:
            return None
        # print(response_amount.text,response_amount.status_code)
        return response_amount
