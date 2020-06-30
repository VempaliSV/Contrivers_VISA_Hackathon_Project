from ma import ma
from models.history import HistoryModel


class HistorySchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        load_instance = True
        model = HistoryModel
        load_only = ("id","mobile_number")

