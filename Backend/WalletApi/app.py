from flask import Flask
from flask_restful import Api
from dotenv import load_dotenv


from db import db
from ma import ma
from resources.wallet import Wallets, Wallet, WalletAmountPay, WalletAmountAdd

app = Flask(__name__)
app.config.from_object("default_config")
api = Api(app)


@app.before_first_request
def create_tables():
    db.create_all()


api.add_resource(Wallets, "/wallets")
api.add_resource(Wallet, "/wallet")
api.add_resource(WalletAmountPay, "/wallet/amount/pay")
api.add_resource(WalletAmountAdd, "/wallet/amount/receive")


if __name__ == "__main__":
    db.init_app(app)
    ma.init_app(app)
    app.run(port=5000, debug=True)


