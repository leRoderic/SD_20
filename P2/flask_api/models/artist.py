from db import db

genres = ('REGGAE', 'POP', 'TRAP', 'HIP HOP', 'ROCK', 'INDIE', 'HEAVY', 'ELECTRONIC', 'OTHER')


class ArtistModel(db.Model):
    __tablename__ = 'artists'  # This is table name

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), unique=True, nullable=False)
    country = db.Column(db.String(30), nullable=False)
    genre = db.Column(db.Enum(*genres), nullable=False)

    def __init__(self, name, country, genre, id=None):
        if id:
            self.id = id
        self.name = name
        self.country = country
        self.genre = genre

    @classmethod
    def find_by_id(cls, idd):
        return db.session.query(ArtistModel).filter_by(id=idd).first()

    @classmethod
    def find_by_name(cls, name):
        return db.session.query(ArtistModel).filter_by(name=" ".join(w.capitalize() for w in name.split(" "))).first()

    @classmethod
    def find_by_country(cls, country):
        return db.session.query(ArtistModel).filter_by(country=" ".join(w.capitalize() for w in country.split(" "))).all()

    @classmethod
    def find_by_genre(cls, genre):
        return db.session.query(ArtistModel).filter_by(genre=genre.upper()).all()

    def save_to_db(self):
        db.session.add(self)
        db.session.commit()

    def delete_from_db(self):
        db.session.query(ArtistModel).filter_by(id=self.id).delete()
        db.session.commit()

    def json(self):
        return {
            "id": self.id,
            "name": self.name,
            "country": self.country,
            "genre": self.genre
        }








