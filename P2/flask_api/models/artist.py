from db import db

genres = ('REGGAE', 'POP', 'TRAP', 'HIP HOP', 'ROCK', 'INDIE', 'HEAVY', 'ELECTRONIC', 'OTHER')


class ArtistModel(db.Model):
    __tablename__ = 'artists'  # This is table name

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(30), unique=True, nullable=False)
    country = db.Column(db.String(30), nullable=False)
    genre = db.Column(db.Enum(*genres), nullable=False)

    def __init__(self, name, country, genre):
        self.name = name
        self.country = country
        self.genre = genre








