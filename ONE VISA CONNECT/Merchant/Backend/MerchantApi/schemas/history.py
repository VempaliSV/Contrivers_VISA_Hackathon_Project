from models.history import HistoryModel
from ma import ma

class HistorySchema(ma.SQLAlchemyAutoSchema):
    class Meta:
        load_instance = True
        model = HistoryModel
        load_only = ("id","mobile_number")