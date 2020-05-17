from flask_restful import Resource, reqparse
from db import db
from models.account import auth
from models.artist import ArtistModel
from models.event import EventModel


class EventArtist(Resource):

    def get(self, id_event, id_artist):
        """
        Retrieve a given artist from a given event.

        :param id_event: event ID
        :param id_artist: artist ID
        :return: artist info in JSON (200) | error event not found (404) | error artist not found in event (404)
        """
        event = EventModel.find_by_id(id_event)
        if event:
            artist = [a for a in event.artists if a.id == int(id_artist)]
            if not artist:
                return {'message': "Artist with id {} not found in event".format(id_artist)}, 404
            return artist[0].json(), 200
        return {'message': "Event with id {} not found".format(id_event)}, 404

    @auth.login_required(role='admin')
    def post(self, id_event):
        """
        Add a new artist to an event.

        :param id_event: event ID
        :return: artist info JSON (200) | error event not found (404) | error artist not found (404) | artist already
                    event (409)
        """
        event = EventModel.find_by_id(id_event)
        if not event:
            return {'message': "Event with id {} not found".format(id_event)}, 404
        data = self.__parse_request__()
        artist = ArtistModel.find_by_name(data.get('name'))
        if not artist:
            # Artist who that does not exist in database cannot be added to an event.
            # artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'))
            # artist.save_to_db()
            return {'message': "Artist does not exist"}, 404
        if event.artist_in_event(data.get('name')):
            return {'message': "Artist already in event"}, 409
        event.artists.append(artist)
        event.save_to_db()
        return artist.json(), 200

    @auth.login_required(role='admin')
    def delete(self, id_event, id_artist):
        """
        Delete given artist from a given event.

        :param id_event: event ID
        :param id_artist: artist ID
        :return: message ok (200) | error artist not found (404) | error event not found (404) | error artist not
                in event (409)
        """
        event = EventModel.find_by_id(id_event)
        if not event:
            return {'message': "Event with id {} not found".format(id_event)}, 404
        artist = ArtistModel.find_by_id(id_artist)
        if not artist:
            return {'message': "Artist does not exist"}, 409
        if not event.artist_in_event(artist.name):
            return {'message': "Artist not found in event"}, 404
        event.artists.remove(artist)
        event.save_to_db()
        return {'message': "Artist with id {} deleted from event".format(id_artist)}, 200

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


class EventArtistsList(Resource):

    def get(self, id):
        """
        Retrieve all artists in a given event.

        :param id: event ID
        :return: event's artists in JSON (200) | error event not found (404)
        """
        event = EventModel.find_by_id(id)
        if event:
            return {"artists": [a.json()['artist'] for a in event.artists]}, 200
        return {'message': "Event with id {} not found".format(id)}, 404


class EventList(Resource):

    def get(self):
        """
        Show all events.

        :return: all events in JSON structure (200)
        """
        return {'events': [i.json()['event'] for i in db.session.query(EventModel).all()]}, 200


class Event(Resource):

    def get(self, id):
        """
        Retrieve event given its ID.

        :param id: event ID
        :return: event info in JSON (200) | error not found (404)
        """
        event = EventModel.find_by_id(id)
        if event:
            return event.json(), 200
        return {'message': "Event with id {} not found".format(id)}, 404

    @auth.login_required(role='admin')
    def post(self, id=None):
        """
        Add a new event given all its info. Optionally, the ID can also be given and will be used if it has not been
        used already (this was a requirement on one of the first sessions).

        :param id: event ID [OPTIONAL]
        :return: event info in a JSON structure (200) | error ID already taken (409)
        """
        if EventModel.find_by_id(id):
            return {'message': "An event with id {} already exists".format(id)}, 409
        data = self.__parse_request__()
        new_event = EventModel(data.get('name'), data.get('place'), data.get('city'), data.get('date'),
                               data.get('price'), data.get('total_available_tickets'), id)
        new_event.save_to_db()
        return new_event.json(), 200

    @auth.login_required(role='admin')
    def delete(self, id):
        """
        Delete an event given its ID.

        :param id: event ID
        :return: message ok (200) | error not found (404)
        """
        to_delete = EventModel.find_by_id(id)
        if not to_delete:
            return {'message': "There is no event with id {}, therefore it cannot be deleted".format(id)}, 404
        to_delete.delete_from_db()
        return {'message': "Event with id {} has benn successfully been deleted".format(id)}, 200

    @auth.login_required(role='admin')
    def put(self, id):
        """
        Add a new event  with the given ID if it does not exist, otherwise modify event with the given ID with the new
        given data.

        :param id: event ID
        :return: created or modified event info in a JSON structure (200)
        """
        data = self.__parse_request__()
        existing = EventModel.find_by_id(id)
        if existing:
            existing.delete_from_db()
        new_event = EventModel(data.get('name'), data.get('place'), data.get('city'), data.get('date'),
                               data.get('price'), data.get('total_available_tickets'), id)
        new_event.save_to_db()
        return new_event.json(), 200

    def __parse_request__(self):
        """
        Parses JSON data from request.

        :return: parsed data
        """
        parser = reqparse.RequestParser()
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('place', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('city', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('date', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('price', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('total_available_tickets', type=str, required=True, help="This field cannot be left blank")
        return parser.parse_args()
