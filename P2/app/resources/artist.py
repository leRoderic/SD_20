from flask_restful import Resource, reqparse
from db import db
from lock import lock
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
            return {'message': "Artist with ['id': {}] not found".format(id)}, 404
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
        return {'message': "Artist with ['id': {}] not found".format(id)}, 404

    @auth.login_required(role='admin')
    def post(self):
        """
        Add a new artist given all its info. Optionally, the ID can also be given and will be used if it has not been
        used already (this was a requirement on one of the first sessions).

        :param id: artist ID [OPTIONAL]
        :return: artist info in a JSON structure (201) | error ID already taken (409)
        """
        data = self.__parse_request__()
        with lock.lock:
            art = ArtistModel.find_by_name(data.get('name'))
            if art:
                return {'message': "An artist with ['name': {}] already exists".format(art.name)}, 409
            new_artist = ArtistModel(" ".join(w.capitalize() for w in data.get('name').split(" ")),
                                     data.get('country'), data.get('genre'))
            new_artist.save_to_db()
            return new_artist.json(), 201

    @auth.login_required(role='admin')
    def delete(self, id):
        """
        Delete an artist given its ID.

        :param id: artist ID
        :return: message ok (200) | error not found (404)
        """
        with lock.lock:
            to_delete = ArtistModel.find_by_id(id)
            if not to_delete:
                return {'message': "There is no artist with ['id': {}], therefore it cannot be deleted".format(id)}, 404
            to_delete.delete_from_db()
            return {'message': "Artist with ['name': {}, 'id': {}] has successfully been deleted".format(to_delete.name, id)}, 200

    @auth.login_required(role='admin')
    def put(self, id):
        """
        Modify artist with the given ID with the new given data.

        :param id: artist ID
        :return: created or modified artist info in a JSON structure (200) | bad request (400) | artist not found (404)
        """
        data = self.__parse_request__()
        with lock.lock:
            existing = ArtistModel.find_by_id(id)
            if not existing:
                return {"message": "Artist with ['id': {}] not found".format(id)}, 404
            existing.delete_from_db()
            new_artist = ArtistModel(" ".join(w.capitalize() for w in data.get('name').split(" ")),
                                     data.get('country'), data.get('genre'), id)
            new_artist.save_to_db()
            return new_artist.json(), 200

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('name', type=str, required=True, help="Operation not valid: 'name' not provided")
        parser.add_argument('country', type=str, required=True, help="Operation not valid: 'country' not provided")
        parser.add_argument('genre', type=str, required=True, help="Operation not valid: 'genre' not provided")
        return parser.parse_args()
