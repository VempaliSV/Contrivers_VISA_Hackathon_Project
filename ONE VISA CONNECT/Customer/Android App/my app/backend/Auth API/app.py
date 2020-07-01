from flask import Flask, jsonify
from flask_restful import Api
from flask_jwt_extended import JWTManager
from marshmallow import ValidationError

from ma import ma
from db import db
from blacklist import BLACKLIST
from resources.user import User, UserRegister, UserLogin, UserLogout, TokenRefresh, UserConfirm
from resources.virtualCard import VirtualCard, Payment
from resources.history import History
from resources.visaNet import VisaNet,Confirmation

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///data.db"
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["PROPAGATE_EXCEPTIONS"] = True
app.config["JWT_BLACKLIST_ENABLED"] = True  # enable blacklist feature
app.config["JWT_BLACKLIST_TOKEN_CHECK"] = [
    "access",
    "refresh"
]  # allowing blacklisting for access and refresh tokens
app.secret_key = "visa"
api = Api(app)


@app.before_first_request
def create_tables():
    db.create_all()


@app.errorhandler(ValidationError)
def handle_marshmallow_validation(err):
    return jsonify(err.messages), 400


jwt = JWTManager(app)


@jwt.token_in_blacklist_loader
def check_if_token_in_blacklist(decrypted_token):
    return decrypted_token["jti"] in BLACKLIST


api.add_resource(UserRegister, "/register")
api.add_resource(UserLogin, "/login")
api.add_resource(UserLogout, "/logout")
api.add_resource(User, "/user")
api.add_resource(TokenRefresh, "/refresh")
api.add_resource(UserConfirm, "/otp")
api.add_resource(VirtualCard, "/virtual_card")
# api.add_resource(AddAmount, "/virtual_card/add_amount/<string:mobile_number>")
api.add_resource(Payment, "/virtual_card/payment")
api.add_resource(History, "/payment/history")
api.add_resource(VisaNet,"/visa_net/payment")
api.add_resource(Confirmation,"/visa_net/confirm/payment")

if __name__ == "__main__":
    db.init_app(app)
    ma.init_app(app)
    app.run(port=5001,debug=True)