from flask_restful import Resource, reqparse
from db import db
from models.account import AccountsModel, auth, g
from models.event import EventModel
from models.order import OrdersModel


class OrdersList(Resource):

    def get(self):
        """
        Return all orders.

        :return: all orders in JSON (200)
        """
        return {'orders': [a.json()['order'] for a in db.session.query(OrdersModel).all()]}, 200


class Orders(Resource):

    def get(self, username):
        """
        Get all orders from a given user.

        :param username: the user
        :return: orders in JSON (200) | error user not found (404) | error no user has no orders (409)
        """
        if not AccountsModel.find_by_username(username):
            return {'message': "User {} not found".format(username)}, 404
        ordrs = OrdersModel.find_by_username(username)
        if not ordrs:
            return {'message': "User {} has no orders".format(username)}, 409
        return {'orders': [a.json()['order'] for a in ordrs]}, 200

    @auth.login_required()
    def post(self, username):
        """
        Add an order to a given user.

        :param username: the user
        :return: order in JSON (200) | error user not found (404)
                                     | error tokens do not match (400)
                                     | event in order not found (404)
                                     | error not enough tickets available (409)
                                     | error not enough money available (409)
        """
        user = AccountsModel.find_by_username(username)
        if not user:
            return {'message': "User {} not found".format(username)}, 404
        if username != g.user.username:
            return {'message': "Usernames used in endpoint and token generation do not match"}, 400
        data = self.__parse_request__()
        ev = EventModel.find_by_id(data.get('event_id'))
        if not ev:
            return {'message': "Event with id {} not found".format(data.get('event_id'))}, 404

        if user.available_money >= (ev.price*data.get('tickets_bought')):
            if data.get('tickets_bought') <= ev.total_available_tickets:
                ev.total_available_tickets -= data.get('tickets_bought')
                ev.save_to_db()
                user.available_money -= ev.price*data.get('tickets_bought')
                order = OrdersModel(user.username, ev.id, data.get('tickets_bought'))
                order.save_to_db()
                user.orders.append(order)
                user.save_to_db()
                return order.json(), 200
            return {'message': "Not enough available tickets in event."}, 409
        return {'message': "User does not have enough money to purchase order."}, 409

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('event_id', type=int, required=True, help="This field cannot be left blank")
        parser.add_argument('tickets_bought', type=int, required=True)
        return parser.parse_args()
