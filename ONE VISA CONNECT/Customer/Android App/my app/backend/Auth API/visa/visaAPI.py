import requests
import os

userName = '8GFE0M5K05DRU333DPRB21haM32rE3BRpRxawIqOoTTlE1h78'
password = 'EhjXxl6b6'

certificatePath = './VisaCert/cert.pem'
privateKeyPath = './VisaCert/privateKey.pem'
caCertPath = "./VisaCert/server.pem"
path = os.path.abspath("server.pem")

url = "https://sandbox.api.visa.com/visadirect/mvisa/v1/merchantpushpayments"
headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 10


class MVisa:

    @classmethod
    def merchant_push_payments_post_payload(cls, payload):
        try:
            response = requests.post(
                url,
                verify=caCertPath,
                cert=(certificatePath, privateKeyPath),
                headers=headers,
                auth=(userName, password),
                json=payload,
                timeout=timeout
            )
        except Exception as e:
            print(e)
            return None

        return response

    @classmethod
    def merchant_push_payments_get_response(cls, status_identifier):

        url_merchant = url + '/' + status_identifier

        try:
            response = requests.post(
                url_merchant,
                verify=caCertPath,
                cert=(certificatePath, privateKeyPath),
                headers=headers,
                auth=(userName, password),
                timeout=timeout
            )
        except Exception as e:
            print(e)
            return None

        return response