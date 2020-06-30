import os
import requests

userName = os.getenv("VISA_USER_NAME")
password = os.getenv("PASSWORD")

certificatePath = './VisaCert/cert.pem'  # insert cert.pem issued in VisaCert folder
privateKeyPath = './VisaCert/privateKey.pem'  # insert privateKey.pem issued in VisaCert folder
caCertPath = "./VisaCert/server.pem"  # insert server.pem issued in VisaCert folder

url = "Enter MVisa Merchant Push Payment URL"
headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 10


class MVisa:
    """
    Interacts with MVisa API.
    """

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
            return None

        return response
