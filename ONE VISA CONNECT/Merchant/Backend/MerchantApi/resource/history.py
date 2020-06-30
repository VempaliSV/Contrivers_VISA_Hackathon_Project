from flask_restful import Resource
from flask import request
from flask_jwt_extended import jwt_required, get_jwt_identity
from models.history import HistoryModel
from models.merchant import MerchantModel
from schemas.history import HistorySchema
from schemas.merchant import MerchantSchema

merchant_schema= MerchantSchema()
history_schema = HistorySchema()
INTERNAL_SERVER_ERROR = 'INTERNAL SERVER ERROR'


class History(Resource):
    @classmethod
    @jwt_required
    def get(cls):
        _id = get_jwt_identity()
        merchant = MerchantModel.find_merchant_by_id(_id)
        print(merchant_schema.dump(merchant))
        try:
            history_details = HistoryModel.find_by_mobile_number(merchant.mobile_number)
        except:
            return {'message': INTERNAL_SERVER_ERROR}, 500

        return {'history': [history_schema.dump(hist) for hist in history_details]}, 200
