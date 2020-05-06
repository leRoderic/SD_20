from flask_restful import Resource, reqparse
from flask import g
from db import db
from models.account import AccountsModel, auth


class Login(Resource):

    # @auth.login_required(role='user')
    def post(self):
        data = self.__parse_request__()
        username, password = data.get('username'), data.get('password')
        user = AccountsModel.find_by_username(username)

        if not user:
            return {'message': "User {} not found".format(username)}, 404

        if not user.verify_password(password):
            return {'message': "Passwords do not match"}, 400
        '''
        if username != g.user.username:
            return {'message': "Usernames used in endpoint and token generation do not match"}, 400
        '''
        return {'token': user.generate_auth_token().decode('ascii')}, 200

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        parser.add_argument('username', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('password', type=str, required=True)
        # list of strings
        return parser.parse_args()
