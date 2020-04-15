class EventList(Resource):

    def get(self):
        return events


api.add_resource(EventList, '/events')


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
