import os
from flask import Flask, jsonify
from flask_restful import Api
from flask_jwt_extended import JWTManager
from marshmallow import ValidationError

from ma import ma
from db import db
from blacklist import BLACKLIST

from resource.merchant import (
    MerchantRegister, 
    MerchantLogin, 
    MerchantLogout, 
    Merchant, 
    TokenRefresh, 
    ReceivePayment
)

from resource.history import History
from dotenv import load_dotenv
load_dotenv()

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = os.environ.get("DATABASE_URI")
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["PROPAGATE_EXCEPTIONS"] = True
app.config["JWT_BLACKLIST_ENABLED"] = True  # enable blacklist feature
app.config["JWT_BLACKLIST_TOKEN_CHECK"] = [
    "access",
    "refresh"
]  # allowing blacklisting for access and refresh tokens
app.secret_key = os.environ.get("APP_SECRET_KEY")
api = Api(app)


@app.before_first_request
def create_tables():
    db.create_all()


jwt = JWTManager(app)


@jwt.token_in_blacklist_loader
def check_if_token_in_blacklist(decrypted_token):
    return decrypted_token["jti"] in BLACKLIST


api.add_resource(MerchantRegister, "/register")
api.add_resource(MerchantLogin, "/login")
api.add_resource(MerchantLogout, "/logout")
api.add_resource(Merchant, "/merchant")
api.add_resource(TokenRefresh, "/refresh")
api.add_resource(ReceivePayment, "/payment")
api.add_resource(History, "/transaction/history")


if __name__ == "__main__":
    db.init_app(app)
    ma.init_app(app)
    app.run(port=5002, debug=True)
