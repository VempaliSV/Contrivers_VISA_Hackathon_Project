from twilio.rest import Client


class OTP:
    TWILIO_ACCOUNT_SID = 'ACaecc396f6f635b3bc0300033b5043b7f'
    TWILIO_AUTH_TOKEN = 'fa5750a0ec3eb03ce41e6b28fb53fe34'
    FROM_PHONE_NUMBER = '+12058756610'

    @classmethod
    def send_otp(cls, mobile_number, text):
        otp_client = Client(cls.TWILIO_ACCOUNT_SID, cls.TWILIO_AUTH_TOKEN)

        message = otp_client.messages.create(
                             body=text,
                             from_=cls.FROM_PHONE_NUMBER,
                             to=mobile_number
                         )

        return message
