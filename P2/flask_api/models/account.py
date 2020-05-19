from db import db, secret_key
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from flask_httpauth import HTTPBasicAuth
from flask import g
from models import order

auth = HTTPBasicAuth()


class AccountsModel(db.Model):
    __tablename__ = 'accounts'
    username = db.Column(db.String(30), primary_key=True, unique=True, nullable=False)
    password = db.Column(db.String(), nullable=False)

    is_admin = db.Column(db.Integer, nullable=False)  # 0: !admin | 1: admin
    available_money = db.Column(db.Integer)
    orders = db.relationship('OrdersModel', backref=db.backref('orders'), lazy=True)

    def __init__(self, username, available_money=200, is_admin=0):
        self.username = username
        self.available_money = available_money
        self.is_admin = is_admin

    @classmethod
    def find_by_username(cls, username):
        """
        Find an account by its username.

        :param username:
        :return:
        """
        return db.session.query(AccountsModel).filter_by(username=username).first()

    @classmethod
    def verify_auth_token(cls, token):
        """
        Verify token matches account.

        :param token: the token
        :return: matched account
        """
        s = Serializer(secret_key)
        try:
            data = s.loads(token)
        except SignatureExpired:
            return None
        except BadSignature:
            return None

        return cls.find_by_username(data['username'])

    def generate_auth_token(self, expiration=600):
        """
        Generate a token.

        :param expiration: token expiration
        :return: token
        """
        s = Serializer(secret_key, expires_in=expiration)
        return s.dumps({'username': self.username})

    def hash_password(self, password):
        """
        Hash password.

        :param password: the password
        """
        self.password = pwd_context.encrypt(password)

    def verify_password(self, password):
        """
        Check if a password matches.

        :param password: the password
        :return: true if it matches false otherwise
        """
        return pwd_context.verify(password, self.password)

    def save_to_db(self, pswd=None):
        """
        Saves the object to the database.

        :param pswd: password in case the account had'nt one
        """
        if not self.password and pswd:
            self.hash_password(pswd)
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        """
        Delete object from database.
        """
        db.session.query(AccountsModel).filter_by(username=self.username).delete()
        db.session.commit()

    def json(self):
        """
        AccountModel to JSON.

        :return: object info in JSON format.
        """
        return {"account": {
            "username": self.username,
            "is_admin": self.is_admin,
            "available_money": self.available_money
        }}


@auth.verify_password
def verify_password(token, password):
    """
    Verify if password match.

    :param token: account token
    :param password: nvmd
    :return: the user if it matches
    """
    user = AccountsModel.verify_auth_token(token)
    if user:
        g.user = user
        return user


@auth.get_user_roles
def get_user_roles(user):
    """
    Retrieve account role.
    :param user: the account
    :return: account's role
    """
    return ['admin'] if user.is_admin == 1 else ['user']
