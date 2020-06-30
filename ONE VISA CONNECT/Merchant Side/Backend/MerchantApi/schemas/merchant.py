from ma import ma
from models.merchant import MerchantModel


class MerchantSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        load_instance = True
        model = MerchantModel
        load_only = ("password",)
        dump_only = ("id",)