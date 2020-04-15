class ArtistList(Resource):

    def get(self):
        return artists


api.add_resource(ArtistList, '/artists')


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
