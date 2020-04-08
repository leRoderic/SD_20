from flask import *
from flask_restful import Resource, Api, reqparse

from data import artists, events

app = Flask(__name__)
api = Api(app)


class ArtistList(Resource):

    def get(self):
        return artists


api.add_resource(ArtistList, '/artists')


class EventList(Resource):

    def get(self):
        return events


api.add_resource(EventList, '/events')


class Artist(Resource):

    def get(self, id):
        artist = self.__get_artist__(id)
        if artist:
            return {'artist': artist}, 200
        return {'message': "Artist with id {} not found".format(id)}, 404

    def post(self, id=None):
        if self.__get_artist__(id):
            return {'message': "An artist with id {} already exists".format(id)}, 406  # Not acceptable
        id = len(artists) if not id else id
        data = self.__parse_request__()
        artists.append({'id': id, 'name': data.get('name'), 'country': data.get('country'), 'genre': data.get('genre')})
        return data, 200

    def delete(self, id):
        to_delete = self.__get_artist__(id)
        if not to_delete:
            return {'message': "There is no artist with id {}, therefore it cannot be deleted".format(id)}, 406
        artists.remove(to_delete)
        return {'message': "Artist with id {} has benn successfully been deleted".format(id)}, 200

    def put(self, id):
        data = self.__parse_request__()
        new_artist = {'id': id, 'name': data.get('name'), 'country': data.get('country'), 'genre': data.get('genre')}
        existing = self.__get_artist__(id)
        if existing:
            artists.remove(existing)
        artists.append(new_artist)
        return new_artist, 200

    def __get_artist__(self, id):
        return next(filter(lambda x: x['id'] == id, artists), None)

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        parser.add_argument('name', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str)
        parser.add_argument('genre', type=str, action="append")  # action = "append" is needed to determine that is a
        # list of strings
        return parser.parse_args()


api.add_resource(Artist, '/artist/<int:id>', '/artist')


class Event(Resource):

    def get(self, id):
        event = self.__get_event__(id)
        if event:
            return {'event': event}, 200
        return {'message': "Event with id {} not found".format(id)}, 404

    def post(self, id=None):
        if self.__get_event__(id):
            return {'message': "An event with id {} already exists".format(id)}, 406  # Not acceptable
        id = len(events) if not id else id
        data = self.__parse_request__()
        events.append({'id': id, 'place': data.get('place'), 'city': data.get('city'), 'country': data.get('country'),
                       'date': data.get('date'), 'artists': []})
        return data, 200

    def delete(self, id):
        to_delete = self.__get_event__(id)
        if not to_delete:
            return {'message': "There is no event with id {}, therefore it cannot be deleted".format(id)}, 406
        events.remove(to_delete)
        return {'message': "Event with id {} has benn successfully been deleted".format(id)}, 200

    def put(self, id):
        data = self.__parse_request__()
        new_event = {'id': id, 'place': data.get('place'), 'city': data.get('city'), 'country': data.get('country'),
                      'date': data.get('date'), 'artists': []}
        existing = self.__get_event__(id)
        if existing:
            events.remove(existing)
        events.append(new_event)
        return new_event, 200

    def __get_event__(self, id):
        return next(filter(lambda x: x['id'] == id, events), None)

    def __parse_request__(self):
        parser = reqparse.RequestParser()  # create parameters parser from request
        # define al input parameters need and its type
        parser.add_argument('place', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('city', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('country', type=str, required=True, help="This field cannot be left blank")
        parser.add_argument('date', type=str, required=True, help="This field cannot be left blank")
        # list of strings
        return parser.parse_args()


api.add_resource(Event, '/event/<int:id>', '/event')

if __name__ == '__main__':
    app.run(port=5000, debug=True)
