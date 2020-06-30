import requests
import pprint as pp
import json
import time
from flask import request, Flask, jsonify
from flask_restful import Resource, Api

userName = '8GFE0M5K05DRU333DPRB21haM32rE3BRpRxawIqOoTTlE1h78'
password = 'EhjXxl6b6'

certificatePath = './VisaCert/cert.pem'
privateKeyPath = './VisaCert/privateKey.pem'
caCertPath = "./VisaCert/server.pem"

apiKey = 'API_KEY'
sharedSecret = "SHARED_SECRET_KEY"

url = "https://sandbox.api.visa.com/visadirect/fundstransfer/v1/pullfundstransactions"

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
            print(e)
        response = jsonify(r.json())
        response.status_code = r.status_code
        return response

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
            print(e)
            return None

        response = jsonify(r.json())
        response.status_code = r.status_code
        return response