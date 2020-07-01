from db import db


class HistoryModel(db.Model):
    __tablename__ = 'history'
    id = db.Column(db.Integer, primary_key=True)
    amount = db.Column(db.Float, nullable=False)
    transaction_time = db.Column(db.String, nullable=False)
    mobile_number = db.Column(db.String, nullable=False)
    transaction_id = db.Column(db.String)
    merchant_name = db.Column(db.String, nullable=False)
    wallet_name = db.Column(db.String, nullable=False)
    status = db.Column(db.String, nullable=False)

    def __init__(self, amount: float, transaction_time: str, mobile_number: str, transaction_id: str,
                 merchant_name: str, wallet_name: str, status: str):
        self.amount = amount
        self.transaction_time = transaction_time
        self.mobile_number = mobile_number
        self.transaction_id = transaction_id
        self.merchant_name = merchant_name
        self.wallet_name = wallet_name
        self.status = status

    @classmethod
    def find_by_mobile_number(cls, mobile_number: str):
        return cls.query.filter_by(mobile_number=mobile_number).all()

    @classmethod
    def find_by_mobile_status(cls, mobile_number: str, status: str):
        return cls.query.filter_by(mobile_number=mobile_number, status=status).all()

    @classmethod
    def find_by_transaction_id(cls, transaction_id: str):
        return cls.query.filter_by(transaction_id=transaction_id).first()

    def save_to_db(self) -> None:
        db.session.add(self)
        db.session.commit()
