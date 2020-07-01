import os
import requests
import pprint as pp
import json
import time
from flask import request, Flask, jsonify
from flask_restful import Resource, Api

userName = os.environ.get("USERNAME_FOR_VISA_API")
password = os.environ.get("PASSWORD_FOR_VISA_API")

# You need to insert these certificates in VisaCert folder.
certificatePath = './VisaCert/cert.pem'
privateKeyPath = './VisaCert/privateKey.pem'
caCertPath = "./VisaCert/server.pem"


url = "ENTER VISA FUNDS TRANSFER PULL FUNDS URL"

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 10


class FundsTransfer(Resource):
    @classmethod
    def merchant_pull_payments_post_response(cls, payload):
        payload = request.get_json()
        try:
            r = requests.post(url,
                              verify=caCertPath,
                              cert=(certificatePath, privateKeyPath),
                              headers=headers,
                              auth=(userName, password),
                              json=payload,
                              timeout=timeout)
        except Exception as e:
            return None

        return r

    @classmethod
    def merchant_pull_payments_get_response(cls, status_identifier):

        url_merchant = url + '/' + status_identifier

        try:
            r = requests.post(
                url_merchant,
                verify=caCertPath,
                cert=(certificatePath, privateKeyPath),
                headers=headers,
                auth=(userName, password),
                timeout=timeout
            )
        except Exception as e:
            return None

        return r
