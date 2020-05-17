from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel, auth
from models.order import OrdersModel


class AccountsList(Resource):

    def get(self):
        """
        Retrieve all accounts.

        :return: all accounts in JSON (200)
        """
        return {'accounts': [a.json()['account'] for a in db.session.query(AccountsModel).all()]}, 200


class Accounts(Resource):

    def get(self, username):
        """
        Retrieve account data in JSON.

        :param username: account username
        :return: info in JSON (200) | error account not found (404)
        """
        user = AccountsModel.find_by_username(username)
        if not user:
            return {'message': "User {} not found".format(username)}, 404

        return user.json(), 200

    def post(self):
        """
        Add new user.

        :return: new user in JSON (200) | error username already taken (409)
        """
        data = self.__parse_request__()
        acc = AccountsModel.find_by_username(data.get('username'))
        if acc:
            return {'message': "A user with that username already exists, log in or choose another username"}, 409
        acc = AccountsModel(data.get('username'))
        acc.save_to_db(data.get('password'))
        return acc.json(), 200

    @auth.login_required(role='admin')
    def delete(self, username):
        """
        Delete an account given its username.

        :param username: username of the account to delete
        :return: message ok (200) | error account not found (404)
        """
        to_delete = AccountsModel.find_by_username(username)
        if not to_delete:
            return {'message': "There is no user {}, therefore it cannot be deleted".format(username)}, 404
        u_orders = OrdersModel.find_by_username(username)
        for i in u_orders:
            i.delete_from_db()
        to_delete.delete_from_db()
        return {'message': "User {} and its orders has successfully been deleted".format(username)}, 200

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True, help="This field cannot be left blank")
        return parser.parse_args()