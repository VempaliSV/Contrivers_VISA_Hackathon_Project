import base64
from Cryptodome.PublicKey import RSA
from Cryptodome.Cipher import PKCS1_v1_5, PKCS1_OAEP


class Rsa:
    @classmethod
    def encrypt(cls, message):
        key = open("./MLE/public.pem").read()
        key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace(
            "\n", "")
        key = base64.b64decode(key)
        key = RSA.importKey(key)
        cipher = PKCS1_v1_5.new(key)
        encrypted_message = base64.b64encode(cipher.encrypt(bytes(message, 'utf-8'))).decode('utf-8')
        return encrypted_message

    @classmethod
    def decrypt(cls, message):
        key = open("./MLE/private.pem").read()
        key = key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replace(
            "\n", "")
        key = base64.b64decode(key)
        key = RSA.importKey(key)
        cipher = PKCS1_v1_5.new(key)
        decrypted_message = cipher.decrypt(base64.b64decode(message), "Failed to decrypt").decode('utf-8')
        return decrypted_message
