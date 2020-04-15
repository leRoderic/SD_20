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
    artists = db.relationship('ArtistModel', secondary=tags, backref=db.backref('events', lady='dynamic'))

    def __init__(self, id, name, place, city, date, price, total_available_tickets):
        self.id = id
        self.name = name
        self.place = place
        self.city = city
        self.date = date
        self.price = price
        self.total_available_tickets = total_available_tickets
