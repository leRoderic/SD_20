from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel, auth
from models.order import OrdersModel


class AccountsList(Resource):

    def get(self):
        return {'accounts': [a.json() for a in db.session.query(AccountsModel).all()]}, 200


class Accounts(Resource):

    def get(self, username):
        user = AccountsModel.find_by_username(username)
        if not user:
            return {'message': "User {} not found".format(username)}, 404

        return user.json(), 200

    def post(self):
        data = self.__parse_request__()
        acc = AccountsModel.find_by_username(data.get('username'))
        if acc:
            return {'message': "A user with that username already exists, log in or choose another username"}, 409
        acc = AccountsModel(data.get('username'))
        acc.save_to_db(data.get('password'))
        return acc.json(), 200

    @auth.login_required(role='admin')
    def delete(self, username):
        to_delete = AccountsModel.find_by_username(username)
        if not to_delete:
            return {'message': "There is no user {}, therefore it cannot be deleted".format(username)}, 400
        u_orders = OrdersModel.find_by_username(username)
        for i in u_orders:
            i.delete_from_db()
        to_delete.delete_from_db()
        return {'message': "User {} and its orders has successfully been deleted".format(username)}, 200

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        # __init__(self, name, place, city, date, price, total_available_tickets, id=None):

        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True, help="This field cannot be left blank")
        return parser.parse_args()