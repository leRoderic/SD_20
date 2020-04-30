from db import db
from models import order


class AccountsModel(db.Model):
    __tablename__ = 'accounts'
    username = db.Column(db.String(30), primary_key=True, unique=True, nullable=False)
    password = db.Column(db.String(), nullable=False)

    # 0 not admin/ 1 is admin
    is_admin = db.Column(db.Integer, nullable=False)
    available_money = db.Column(db.Integer)
    orders = db.relationship('OrdersModel', backref=db.backref('orders'), lazy=True)

    def __init__(self, username, password, available_money=200, is_admin=0):

        self.username = username
        self.password = password
        self.available_money = available_money
        self.is_admin = is_admin

    @classmethod
    def find_by_username(cls, username):
        return db.session.query(AccountsModel).filter_by(username=username).first()

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.query(AccountsModel).filter_by(username=self.username).delete()
        db.session.commit()

    def json(self):
        return {"user": {
            "username": self.username,
            "available_money": self.available_money,
            "is_admin": self.is_admin
        }}
