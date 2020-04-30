from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel


class AccountsList(Resource):

    def get(self):
        return {'accounts': [a.json() for a in db.session.query(AccountsModel).all()]}, 200
