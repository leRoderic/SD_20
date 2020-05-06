from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel


class AccountsList(Resource):

    def get(self):
        return {'accounts': [a.json() for a in db.session.query(AccountsModel).all()]}, 200


class AccountDetails(Resource):

    def get(self, username):
        user = AccountsModel.find_by_username(username)
        if not user:
            return {'message': "User {} not found".format(username)}, 404

        return user.json(), 200
