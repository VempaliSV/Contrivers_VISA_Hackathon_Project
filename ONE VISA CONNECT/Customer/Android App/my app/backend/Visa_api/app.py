import requests
import pprint as pp
import  json
import time


userName = 'USERNAME'
password = 'PASSWORD'
certificatePath = 'CERTIFICATE'
privateKeyPath = 'PRIVATE_KEY_PATH'
caCertPath = 'caCERT_PATH'

#mleKeyId = 'MLE_KEY_ID'
#decryptionPrivateKeyPath = 'DECRYPTION_PRIVATE_KEY_PATH'
#encryptionPublicKeyPath = 'ENCRYPTION_KEY_PATH'

apiKey = 'API_KEY'
sharedSecret = "SHARED_SECRET_KEY"



url_post = "SANDBOX_URL_CASHINPUSHPAYMENT_POST"
url_get = "SANDBOX_URL_CASHINPUSHPAYMENT_GET"

payload =   {
"acquirerCountryCode": "643",
"acquiringBin": "400171",
"amount": "1240.05",
"businessApplicationId": "CI",
"cardAcceptor": {
"address": {
"city": "Bangalore",
"country": "IN"
},
"idCode": "ID-Code123",
"name": "Test Merchant"
},
"localTransactionDateTime": "2020-06-17T13:05:06",
"merchantCategoryCode": "6012",
"recipientPrimaryAccountNumber": "4123640062698797",
"retrievalReferenceNumber": "430000367618",
"senderAccountNumber": "4541237895236",
"senderName": "Mohammed Qasim",
"senderReference": "1234",
"systemsTraceAuditNumber": "313042",
"transactionCurrencyCode": "840"
}

headers = {'Accept': 'application/json'}
timeout = 10

try:
    r = requests.post(url,
                  verify = (caCertPath),
				  cert = (certificatePath,privateKeyPath),
				  headers = headers,
				  auth = (userName, password),
				#   data = body,
                  json = payload,
                  timeout = timeout)

    print(r.status_code)
    print(r.reason)
    # print(r.Respose)
    r = r.json()
    # print(r)
    pp.pprint(r)
    
except Exception as e:
    print(e)


               

