from db import db
from models.event import EventModel


class OrdersModel(db.Model):
    __tablename__ = 'orders'

    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(30),
                         db.ForeignKey('accounts.username'), nullable=False)
    id_event = db.Column(db.Integer, nullable=False)
    tickets_bought = db.Column(db.Integer, nullable=False)

    def __init__(self, username, id_event, tickets_bought):
        self.username = username
        self.id_event = id_event
        self.tickets_bought = tickets_bought

    @classmethod
    def find_by_username(cls, username):
        """
        Find an order by its account username.

        :param username: account username
        :return: the orders
        """
        return db.session.query(OrdersModel).filter_by(username=username).all()

    def save_to_db(self):
        """
        Saves itself to the database.
        """
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        """"
        Deletes itself from the database.
        """
        db.session.query(OrdersModel).filter_by(id=self.id).delete()
        db.session.commit()

    def json(self):
        """
        OrderModel to JSON.

        :return: OrderModel data in JSON format.
        """
        ev = EventModel.find_by_id(self.id_event)
        return {"order": {
            "id": self.id,
            "username": self.username,
            "event_name": ev.name,
            "event_date": ev.date,
            "event_city": ev.city,
            "id_event": self.id_event,
            "tickets_bought": self.tickets_bought
        }}
