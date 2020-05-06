from flask_restful import Resource, reqparse
from db import db
from models.account import auth
from models.artist import ArtistModel
from models.event import EventModel


class ArtistEventsList(Resource):

    def get(self, id):
        artist = ArtistModel.find_by_id(id)
        if not artist:
            return {'message': "Artist with id {} not found".format(id)}, 404
        events = [a.json() for a in db.session.query(EventModel).all() if a.artist_in_event(artist.name)]
        return {'events': events}, 200


class ArtistList(Resource):

    def get(self):
        return {"artists": [i.json() for i in db.session.query(ArtistModel).all()]}


class Artist(Resource):

    def get(self, id):
        artist = ArtistModel.find_by_id(id)
        if artist:
            return {'artist': artist.json()}, 200
        return {'message': "Artist with id {} not found".format(id)}, 404

    @auth.login_required(role='admin')
    def post(self, id=None):
        if ArtistModel.find_by_id(id):
            return {'message': "An artist with id {} already exists".format(id)}, 409
        data = self.__parse_request__()
        new_artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'), id)
        new_artist.save_to_db()
        return new_artist.json(), 200

    @auth.login_required(role='admin')
    def delete(self, id):
        to_delete = ArtistModel.find_by_id(id)
        if not to_delete:
            return {'message': "There is no artist with id {}, therefore it cannot be deleted".format(id)}, 400
        to_delete.delete_from_db()
        return {'message': "Artist with id {} has benn successfully been deleted".format(id)}, 200

    @auth.login_required(role='admin')
    def put(self, id):
        data = self.__parse_request__()
        existing = ArtistModel.find_by_id(id)
        if existing:
            existing.delete_from_db()
        new_artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'), id)
        new_artist.save_to_db()
        return new_artist.json(), 200

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str)
        parser.add_argument('genre', type=str)  # action = "append" is needed to determine that is a
        # list of strings
        return parser.parse_args()
