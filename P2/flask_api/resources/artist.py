from flask_restful import Resource, reqparse
from db import db
from models.account import auth
from models.artist import ArtistModel
from models.event import EventModel


class ArtistEventsList(Resource):

    def get(self, id):
        """
        Retrieve all events a given artist attends.

        :param id: artist ID
        :return: events in JSON (200) | error artist not found (404)
        """
        artist = ArtistModel.find_by_id(id)
        if not artist:
            return {'message': "Artist with id {} not found".format(id)}, 404
        events = [a.json()['event'] for a in db.session.query(EventModel).all() if a.artist_in_event(artist.name)]
        return {'events': events}, 200


class ArtistList(Resource):

    def get(self):
        """
        Show all artists.

        :return: all artists in JSON structure (200)
        """
        return {"artists": [i.json()['artist'] for i in db.session.query(ArtistModel).all()]}, 200


class Artist(Resource):

    def get(self, id):
        """
        Find artist given its ID.

        :param id: artist's ID
        :return: artist info in a JSON structure (200) | not found error (404)
        """
        artist = ArtistModel.find_by_id(id)
        if artist:
            return artist.json(), 200
        return {'message': "Artist with id {} not found".format(id)}, 404

    @auth.login_required(role='admin')
    def post(self, id=None):
        """
        Add a new artist given all its info. Optionally, the ID can also be given and will be used if it has not been
        used already (this was a requirement on one of the first sessions).

        :param id: artist ID [OPTIONAL]
        :return: artist info in a JSON structure (200) | error ID already taken (409)
        """
        if ArtistModel.find_by_id(id):
            return {'message': "An artist with id {} already exists".format(id)}, 409
        data = self.__parse_request__()
        new_artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'), id)
        new_artist.save_to_db()
        return new_artist.json(), 200

    @auth.login_required(role='admin')
    def delete(self, id):
        """
        Delete an artist given its ID.

        :param id: artist ID
        :return: message ok (200) | error not found (404)
        """
        to_delete = ArtistModel.find_by_id(id)
        if not to_delete:
            return {'message': "There is no artist with id {}, therefore it cannot be deleted".format(id)}, 404
        to_delete.delete_from_db()
        return {'message': "Artist with id {} has benn successfully been deleted".format(id)}, 200

    @auth.login_required(role='admin')
    def put(self, id):
        """
        Add new artist with the given ID if it does not exist, otherwise modify artist with the given ID with the new
        given data.

        :param id: artist ID
        :return: created or modified artist info in a JSON structure (200)
        """
        data = self.__parse_request__()
        existing = ArtistModel.find_by_id(id)
        if existing:
            existing.delete_from_db()
        new_artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'), id)
        new_artist.save_to_db()
        return new_artist.json(), 200

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str)
        parser.add_argument('genre', type=str)
        return parser.parse_args()
