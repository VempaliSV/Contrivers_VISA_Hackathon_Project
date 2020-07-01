import traceback
from marshmallow import INCLUDE, EXCLUDE
from flask import request
from flask_restful import Resource
import datetime

from models.user import UserModel
from models.virtualCard import VirtualCardModel
from schemas.user import UserSchema
from flask_jwt_extended import (
    create_access_token,
    create_refresh_token,
    jwt_refresh_token_required,
    get_jwt_identity,
    jwt_required,
    get_raw_jwt
)
from blacklist import BLACKLIST
from MLE.RSA import Rsa
from libs.security import AESCipher

user_schema = UserSchema(unknown=EXCLUDE)

cipher = AESCipher("mysecretpassword")  # AES encryption key can be replaced by any 16byte string
rsa = Rsa()

# constants
USER_NOT_FOUND = "{} not found"
USER_ALREADY_EXISTS = "{} already exists"
USER_CREATED = "User: {} created successfully"
USER_DELETED = "User: {} deleted successfully"
INVALID_PASSWORD = "Invalid Password"
USER_LOGGED_OUT = "User logged out successfully"
USER_NOT_CONFIRMED = "{}"
USER_CONFIRMED = "{}"
OTP_SENT = "OTP has been sent to {}"
OTP_FAILED = "Couldn't send OTP"


class UserRegister(Resource):
    @classmethod
    def post(cls):
        """
        Registers the user.
        payload = {
        "email": "************",
        "full_name": "*********",
        "password": "*******",
        "mobile_number": "*********",
        "country":"****"
        }

        """
        user = user_schema.load(request.get_json())

        if UserModel.find_user_by_email(user.email):
            return {"msg": USER_ALREADY_EXISTS.format(user.email)}, 400

        if UserModel.find_user_by_mobile_number(user.mobile_number):
            return {"msg": USER_ALREADY_EXISTS.format(user.mobile_number)}, 400

        
        user.save_to_db()
        return {"msg": USER_CREATED.format(user.email)}, 201


class User(Resource):
    @classmethod
    @jwt_required
    def get(cls):
        """"
        get using email (can be changed per use case).
        """
        _id = get_jwt_identity()
        user = UserModel.find_user_by_id(_id)
        if not user:
            return {"msg": USER_NOT_FOUND.format(user.email)}, 404
        return user_schema.dump(user), 200


class UserLogin(Resource):
    @classmethod
    def post(cls):
        """
        Verifies login credentials.
        Token expiry can be altered by the organisation by changing-
        expires = datetime.timedelta(days=*)
        payload = {
        email:"*************",
        password:"**************"
        }
        """
        json_data = request.get_json()
        user_data = user_schema.load(json_data, partial=("full_name", "mobile_number"))
        user = UserModel.find_user_by_email(email=user_data.email)

        if not user:
            return {"msg": USER_NOT_FOUND.format(user_data.email)}, 401

        
        if password != user_data.password:
            return {"msg": INVALID_PASSWORD}, 401
        elif not user.activated:
            return {"msg": USER_NOT_CONFIRMED.format(user.mobile_number)}, 400

        expires = datetime.timedelta(days=1)
        access_token = create_access_token(identity=user.id, expires_delta=expires, fresh=True)
        refresh_token = create_refresh_token(identity=user.id)
        return {"access_token": access_token, "refresh_token": refresh_token, "user": user_schema.dump(user)}, 200


class UserLogout(Resource):
    @classmethod
    @jwt_required
    def post(cls):
        """
        User Logout blacklist's the "jti" which restricts the access.
        """
        jti = get_raw_jwt()["jti"]
        BLACKLIST.add(jti)
        return {"msg": USER_LOGGED_OUT}, 200


class TokenRefresh(Resource):
    @classmethod
    @jwt_refresh_token_required
    def post(cls):
        """
        Issues a new access token.
        :return:
        """
        user_id = get_jwt_identity()
        new_access_token = create_access_token(identity=user_id, fresh=True)  # creating new fresh token
        return {"access_token": new_access_token}, 200


class UserConfirm(Resource):
    @classmethod
    def post(cls):
        """
        Sends the OTP for activation.
        payload = {
        mobile_number:"********"
        }
        """
        json_data = request.get_json()
        user_data = user_schema.load(json_data, partial=("full_name", "email", "password"))
        user = UserModel.find_user_by_mobile_number(mobile_number=json_data["mobile_number"])
        if not user:
            return {"msg": USER_NOT_FOUND.format(json_data["mobile_number"])}, 404
        try:
            message = user.send_otp(otp)
        except:
            traceback.print_exc()
            return {"msg": OTP_FAILED}, 500

        return {"msg": OTP_SENT.format(user.mobile_number)}, 200

    @classmethod
    def put(cls):
        """
        Confirms the registration of user and changing user.activated = True
        payload = {
        mobile_number:"********"
        }
        """
        json_data = request.get_json()
        user_data = user_schema.load(json_data, partial=("full_name", "email", "password"))
        user = UserModel.find_user_by_mobile_number(mobile_number=user_data.mobile_number)
        if not user:
            return {"msg": USER_NOT_FOUND}, 404
        user.activated = True
        user.save_to_db()
        return {"msg": USER_CONFIRMED.format(user.mobile_number)}, 200
