from db import db


class MerchantModel(db.Model):
    __tablename__ = "merchants"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(40), nullable=False)
    email = db.Column(db.String(40), unique=True, nullable=False)
    password = db.Column(db.String(80), nullable=False)
    mobile_number = db.Column(db.String(20), unique=True, nullable=False)
    activated = db.Column(db.Boolean, default=True)
    state = db.Column(db.String(40), nullable=False)
    country = db.Column(db.String(40), nullable=False)
    zipCode = db.Column(db.String, nullable=False)
    acquirerCountryCode = db.Column(db.String, nullable=False)

    acquiringBin = db.Column(db.Integer, default=408999)  # Fixed
    businessApplicationId = db.Column(db.String(2), default="PP")  # Fixed
    terminalId = db.Column(db.String(8), unique=True)
    idCode = db.Column(db.String(15), unique=True)

    @classmethod
    def find_merchant_by_id(cls, _id):
        return cls.query.filter_by(id=_id).first()

    @classmethod
    def find_merchant_by_email(cls, email):
        return cls.query.filter_by(email=email).first()

    @classmethod
    def find_merchant_by_mobile_number(cls, mobile_number):
        return cls.query.filter_by(mobile_number=mobile_number).first()

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()
