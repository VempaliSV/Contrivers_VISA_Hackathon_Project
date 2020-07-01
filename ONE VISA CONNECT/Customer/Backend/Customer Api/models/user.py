from db import db
from libs.OTP import OTP


class UserModel(db.Model):
    """
    User Model
    Stores users details done by this medium
    """
    __tablename__ = "users"
    id = db.Column(db.Integer, primary_key=True)
    full_name = db.Column(db.String(40), nullable=False)
    email = db.Column(db.String(40), unique=True, nullable=False)
    password = db.Column(db.LargeBinary, nullable=False)
    mobile_number = db.Column(db.String(20), unique=True, nullable=False)
    activated = db.Column(db.Boolean, default=False)
    country_code = db.Column(db.String(40))
    # pan details relationship

    @classmethod
    def find_user_by_email(cls, email):
        return cls.query.filter_by(email=email).first()

    @classmethod
    def find_user_by_mobile_number(cls, mobile_number):
        return cls.query.filter_by(mobile_number=mobile_number).first()

    @classmethod
    def find_user_by_id(cls, user_id):
        return cls.query.filter_by(id=user_id).first()

    def send_otp(self, text):
        return None

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.delete(self)
        db.session.commit()