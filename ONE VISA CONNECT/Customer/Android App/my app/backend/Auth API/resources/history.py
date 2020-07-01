from flask_restful import Resource
from flask import request
from flask_jwt_extended import jwt_required
from models.history import HistoryModel
from schemas.history import HistorySchema

history_schema = HistorySchema()
INTERNAL_SERVER_ERROR = 'INTERNAL SERVER ERROR'


class History(Resource):
    @classmethod
    @jwt_required
    def get(cls):
        payload = request.get_json()

        try:
            history_details = HistoryModel.find_by_mobile_number(payload['mobile_number'])
        except:
            return {'message': INTERNAL_SERVER_ERROR}, 500

        return {'history': [history_schema.dump(hist) for hist in history_details]}, 200
