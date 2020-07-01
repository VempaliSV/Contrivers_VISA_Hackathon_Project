from db import db


class VirtualCardModel(db.Model):
    __tablename__ = 'panDetails'
    id = db.Column(db.Integer, primary_key=True)
    pan = db.Column(db.String, unique=True, nullable=False)
    amount = db.Column(db.Integer, default=0)
    card_generated_time = db.Column(db.String, nullable=False)
    last_transact_time = db.Column(db.String)
    count = db.Column(db.Integer, default=0)

    mobile_number = db.Column(db.String, db.ForeignKey('users.mobile_number'))
    user = db.relationship('UserModel')

    def __init__(self, pan: str, card_generation_time, mobile_number: str):
        self.pan = pan
        self.amount = 0
        self.count = 0
        self.card_generated_time = card_generation_time
        self.last_transact_time = None
        self.mobile_number = mobile_number

    @classmethod
    def find_by_mobile_number(cls, mobile_number: str):
        return cls.query.filter_by(mobile_number=mobile_number).first()

    def save_to_db(self) -> None:
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()
