from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel, auth


class Login(Resource):

    def post(self):
        """
        Do login with username and password.

        :return: token for the username
        """
        data = self.__parse_request__()
        username, password = data.get('username'), data.get('password')
        user = AccountsModel.find_by_username(username)

        if not user:
            return {'message': "User {} not found".format(username)}, 404

        if not user.verify_password(password):
            return {'message': "Passwords do not match"}, 400

        return {'token': user.generate_auth_token().decode('ascii')}, 200

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True)
        return parser.parse_args()
