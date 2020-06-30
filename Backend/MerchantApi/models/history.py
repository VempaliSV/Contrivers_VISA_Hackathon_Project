from db import db


class HistoryModel(db.Model):
    __tablename__ = 'history'
    id = db.Column(db.Integer, primary_key=True)
    amount = db.Column(db.Float, nullable=False)
    transaction_time = db.Column(db.String, nullable=False)
    transaction_id = db.Column(db.String)
    merchant_name = db.Column(db.String, nullable=False)
    merchant_mobile_number = db.Column(db.String, nullable=False)
    customer_mobile_number = db.Column(db.String)
    customer_wallet_name = db.Column(db.String)
    status = db.Column(db.String, nullable=False)

    def __init__(self, amount: float, transaction_time: str, transaction_id: str, merchant_mobile_number: str, 
                 merchant_name: str, customer_mobile_number: str, status: str, customer_wallet_name: str):
        self.amount = amount
        self.transaction_time = transaction_time
        self.merchant_mobile_number = merchant_mobile_number
        self.transaction_id = transaction_id
        self.merchant_name = merchant_name
        self.customer_mobile_number = customer_mobile_number
        self.customer_wallet_name = customer_wallet_name
        self.status = status


    @classmethod
    def find_by_mobile_number(cls, mobile_number: str):
        return cls.query.filter_by(merchant_mobile_number=mobile_number).all()

    @classmethod
    def find_by_mobile_status(cls, mobile_number: str, status: str):
        return cls.query.filter_by(merchant_mobile_number=mobile_number, status=status).all()

    @classmethod
    def find_by_transaction_id(cls, transaction_id: str):
        return cls.query.filter_by(transaction_id=transaction_id).first()

    def save_to_db(self) -> None:
        db.session.add(self)
        db.session.commit()
