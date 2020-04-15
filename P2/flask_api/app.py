from flask import *
from flask_restful import Resource, Api, reqparse
from flask_migrate import Migrate
from db import db
from models.artist import ArtistModel
from models.event import EventModel
from data import artists, events

app = Flask(__name__)
api = Api(app)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
migrate = Migrate(app, db)