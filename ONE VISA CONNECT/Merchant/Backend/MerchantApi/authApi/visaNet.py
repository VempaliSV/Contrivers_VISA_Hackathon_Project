import requests

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}
timeout = 20

INTERNAL_SERVER_ERROR = "Internal Server Error"


class VisaNet:
    @classmethod
    def AmountConfirmation(cls, payload):
        """
            Checks the Sender Details By calling auth api and checks
            whether sender has enough money in his wallet or not..
            :param payload: {
                'mobile_number' = ******
                'wallet_name' = *****
                'merchant_name' = *****
                'amount' = *****
                'systemsTraceAuditNumber' = *******
            }
            :return: response from auth api.
        """
        url = "Enter VISA NET URL"

        try:
            r = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except Exception as e:
            return {"msg": INTERNAL_SERVER_ERROR}, 500
        return r

    @classmethod
    def TransactionConfirmation(cls, payload):
        """
            Sends Transaction confirmation to wallet api with the help of status_code in payload field.
            If status_code is 200 then transaction is successful else not and rollback is done.
            :param payload: {
                'mobile_number': ******,
                'pan': ******,
                'systemsTraceAuditNumber': *******,
                'code': *****
            }
            :return: response from auth api.
        """
        url = "ENTER VISA NET CONFIRMATION URL"

        try:
            r = requests.put(url, json=payload, headers=headers, timeout=timeout)
        except Exception as e:
            return {"msg": INTERNAL_SERVER_ERROR}, 500

        return r
