from ma import ma
from models.user import UserModel


class UserSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        load_instance = True
        model = UserModel
        load_only = ("password",)
        dump_only = ("id",)
