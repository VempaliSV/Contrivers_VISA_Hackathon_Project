import requests

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 20

INTERNAL_SERVER_ERROR = "Internal Server Error"


class VisaNet:
    @classmethod
    def AmountConfirmation(cls, payload):
        print("AmountConfirmation")
        url = "https://virtual-card-auth.herokuapp.com/visa_net/payment"
        # url = "http://127.0.0.1:5001/visa_net/payment"
        try:
            r = requests.put(url, json=payload, headers=headers, timeout=timeout)
            return r
        except Exception as e:
            print(e)
            return {"msg": INTERNAL_SERVER_ERROR}, 500

    @classmethod
    def TransactionConfirmation(cls, payload):
        print("Confirmation")
        url = "https://virtual-card-auth.herokuapp.com/visa_net/confirm/payment"
        # url = "http://127.0.0.1:5001/visa_net/confirm/payment"
        try:
            r = requests.put(url, json=payload, headers=headers, timeout=timeout)
            return r
        except Exception as e:
            print(e)
            return {"msg": INTERNAL_SERVER_ERROR}, 500