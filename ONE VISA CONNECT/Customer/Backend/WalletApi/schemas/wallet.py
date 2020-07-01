from ma import ma
from models.wallet import WalletModel


class WalletSchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        model = WalletModel
        load_only = ("id",)
        load_instance = True