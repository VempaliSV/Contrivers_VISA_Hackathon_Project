from db import db


class WalletModel(db.Model):
    __tablename__ = 'wallets'
    id = db.Column(db.Integer, primary_key=True)
    mobile_number = db.Column(db.String, unique=True, nullable=False)
    kyc_status = db.Column(db.Boolean, default=False, nullable=False)
    amount = db.Column(db.Float, default=0, nullable=False)

    @classmethod
    def find_by_mobile_number(cls, mobile_no: str) -> "WalletModel":
        return cls.query.filter_by(mobile_number=mobile_no).first()

    def reduce_amount(self, amount:int):
        self.amount = (int(self.amount) - amount)

    def add_amount(self, amount:int):
        self.amount = (int(self.amount) + amount)

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()
