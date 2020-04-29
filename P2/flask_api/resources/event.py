from flask_restful import Resource, reqparse
from db import db
from models.artist import ArtistModel
from models.event import EventModel


class EventArtist(Resource):

    def get(self, id_event, id_artist):
        event = EventModel.find_by_id(id_event)
        if event:
            artist = [a for a in event.artists if a.id == int(id_artist)]
            if not artist:
                return {'message': "Artist with id {} not found in event".format(id_artist)}, 404
            return {"artist": artist[0].json()}, 200
        return {'message': "Event with id {} not found".format(id_event)}, 404

    def post(self, id_event):
        event = EventModel.find_by_id(id_event)
        if not event:
            return {'message': "Event with id {} not found".format(id_event)}, 404
        data = self.__parse_request__()
        artist = ArtistModel.find_by_name(data.get('name'))
        if not artist:
            artist = ArtistModel(data.get('name'), data.get('country'), data.get('genre'))
            artist.save_to_db()
        if event.artist_in_event(data.get('name')):
            return {'message': "Artist already in event"}, 409
        event.artists.append(artist)
        event.save_to_db()
        return {"artist": artist.json()}, 200

    def delete(self, id_event, id_artist):
        event = EventModel.find_by_id(id_event)
        if not event:
            return {'message': "Event with id {} not found".format(id_event)}, 404
        artist = ArtistModel.find_by_id(id_artist)
        if not artist:
            return {'message': "Artist does not exist"}, 400
        if not event.artist_in_event(artist.name):
            return {'message': "Artist not found in event"}, 404
        event.artists.remove(artist)
        event.save_to_db()
        return {'message': "Artist with id {} deleted from event".format(id_artist)}, 404

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str)
        parser.add_argument('genre', type=str)  # action = "append" is needed to determine that is a
        # list of strings
        return parser.parse_args()


class EventArtistsList(Resource):

    def get(self, id):
        event = EventModel.find_by_id(id)
        if event:
            return {"artists": [a.json() for a in event.artists]}, 200
        return {'message': "Event with id {} not found".format(id)}, 404


class EventList(Resource):

    def get(self):
        return {'events': [i.json() for i in db.session.query(EventModel).all()]}, 200


class Event(Resource):

    def get(self, id):
        event = EventModel.find_by_id(id)
        if event:
            return event.json(), 200
        return {'message': "Event with id {} not found".format(id)}, 404

    def post(self, id=None):
        if EventModel.find_by_id(id):
            return {'message': "An event with id {} already exists".format(id)}, 409
        data = self.__parse_request__()
        new_event = EventModel(data.get('name'), data.get('place'), data.get('city'), data.get('date'),
                               data.get('price'), data.get('total_available_tickets'), id)
        new_event.save_to_db()
        return new_event.json(), 200

    def delete(self, id):
        to_delete = EventModel.find_by_id(id)
        if not to_delete:
            return {'message': "There is no event with id {}, therefore it cannot be deleted".format(id)}, 400
        to_delete.delete_from_db()
        return {'message': "Event with id {} has benn successfully been deleted".format(id)}, 200

    def put(self, id):
        data = self.__parse_request__()

        existing = EventModel.find_by_id(id)
        if existing:
            existing.delete_from_db()
        new_event = EventModel(data.get('name'), data.get('place'), data.get('city'), data.get('date'),
                               data.get('price'), data.get('total_available_tickets'), id)
        new_event.save_to_db()
        return new_event.json(), 200

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        # __init__(self, name, place, city, date, price, total_available_tickets, id=None):

        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('place', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('city', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('date', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('price', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('total_available_tickets', type=str, required=True, help="This field cannot be left blank")
        return parser.parse_args()
