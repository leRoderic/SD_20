from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api
from flask_migrate import Migrate
from db import create_app
from resources.artist import ArtistList, Artist, ArtistEventsList
from resources.event import EventList, Event, EventArtistsList, EventArtist

app = create_app()
app.app_context().push()
db = SQLAlchemy(app)
api = Api(app)
migrate = Migrate(app, db)

api.add_resource(EventArtist, '/event/<int:id_event>/artist/<id_artist>','/event/<int:id_event>/artist')
api.add_resource(EventArtistsList, '/event/<int:id>/artists')
api.add_resource(EventList, '/events')
api.add_resource(Event, '/event/<int:id>', '/event')
api.add_resource(ArtistEventsList, '/artist/<int:id>/events')
api.add_resource(ArtistList, '/artists')
api.add_resource(Artist, '/artist/<int:id>', '/artist')
