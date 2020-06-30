from flask_restful import Resource
from flask import request
from flask_jwt_extended import jwt_required, get_jwt_identity
from models.history import HistoryModel
from models.user import UserModel
from schemas.history import HistorySchema

history_schema = HistorySchema()
INTERNAL_SERVER_ERROR = 'INTERNAL SERVER ERROR'


class History(Resource):
    @classmethod
    @jwt_required
    def get(cls):
        """
        Fetches the history of a particular user from database
        """
        _id = get_jwt_identity()
        user = UserModel.find_user_by_id(_id)

        try:
            history_details = HistoryModel.find_by_mobile_number(user.mobile_number)
        except:
            return {'message': INTERNAL_SERVER_ERROR}, 500

        return {'history': [history_schema.dump(hist) for hist in history_details]}, 200
