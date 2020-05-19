from models.artist import ArtistModel
from db import db

tags = db.Table('tags', db.Column('event_id', db.Integer, db.ForeignKey('events.id')),
                db.Column('artist_id', db.Integer, db.ForeignKey('artists.id')))


class EventModel(db.Model):
    __tablename__ = 'events'
    __table_args__ = (db.UniqueConstraint('name', 'date', 'city'),)

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), nullable=False)
    place = db.Column(db.String(30), nullable=False)
    city = db.Column(db.String(30), nullable=False)
    date = db.Column(db.String(30), nullable=False)
    price = db.Column(db.Integer, nullable=False)
    total_available_tickets = db.Column(db.Integer, nullable=False)
    artists = db.relationship('ArtistModel', secondary=tags, backref=db.backref('events', lazy='dynamic'))

    def __init__(self, name, place, city, date, price, total_available_tickets, id=None):
        if id:
            self.id = id
        self.name = name
        self.place = place
        self.city = city
        self.date = date
        self.price = price
        self.total_available_tickets = total_available_tickets

    @classmethod
    def find_by_id(cls, idd):
        """
        Find event given its ID.

        :param idd: event ID
        :return: the event
        """
        return db.session.query(EventModel).filter_by(id=idd).first()

    @classmethod
    def find_by_name(cls, name):
        """
        Find event given its name.

        :param name: event name
        :return: the event
        """
        return db.session.query(EventModel).filter_by(name=" ".join(w.capitalize() for w in name.split(" "))).first()

    @classmethod
    def find_by_place(cls, place):
        """
        Find event by its place.

        :param place: event place
        :return: the event
        """
        return db.session.query(EventModel).filter_by(place=" ".join(w.capitalize() for w in place.split(" "))).all()

    @classmethod
    def find_by_city(cls, city):
        """
        Find event by its city.

        :param city: event city
        :return: the event
        """
        return db.session.query(EventModel).filter_by(city=" ".join(w.capitalize() for w in city.split(" "))).all()

    def artist_in_event(self, name):
        """
        Check if a given artist is in the event.

        :param name: artist name
        :return: true if it is false otherwise
        """
        return name in [a.name for a in self.artists]

    def save_to_db(self):
        """
        Saves itself to the database.
        """
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        """
        Deletes itself from the database.
        """
        db.session.query(EventModel).filter_by(id=self.id).delete()
        db.session.commit()

    def json(self):
        """
        EventModel to JSON.

        :return: event info in JSON format.
        """
        return {"event": {
            "id": self.id,
            "name": self.name,
            "place": self.place,
            "city": self.city,
            "date": self.date,
            "artists": [a.json()['artist'] for a in self.artists],
            "price": self.price,
            "total_available_tickets": self.total_available_tickets
        }}
