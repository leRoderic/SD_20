from flask_restful import Api
from flask_migrate import Migrate
from flask import render_template
from db import create_app, db
from resources.accounts import AccountsList, Accounts
from resources.artist import ArtistList, Artist, ArtistEventsList
from resources.event import EventList, Event, EventArtistsList, EventArtist
from resources.login import Login
from resources.orders import Orders, OrdersList

app = create_app()
app.app_context().push()
api = Api(app)
# Migration imports, DO NOT REMOVE
from models.artist import ArtistModel
from models.event import EventModel
from models.account import AccountsModel
from models.order import OrdersModel
migrate = Migrate(app, db)

api.add_resource(EventArtist, '/event/<int:id_event>/artist/<id_artist>', '/event/<int:id_event>/artist')
api.add_resource(EventArtistsList, '/event/<int:id>/artists')
api.add_resource(EventList, '/events')
api.add_resource(Event, '/event/<int:id>', '/event')
api.add_resource(ArtistEventsList, '/artist/<int:id>/events')
api.add_resource(ArtistList, '/artists')
api.add_resource(Artist, '/artist/<int:id>', '/artist')
api.add_resource(Orders, '/orders/<string:username>')
api.add_resource(OrdersList, '/orders')
api.add_resource(AccountsList, '/accounts')
api.add_resource(Accounts, '/account', '/account/<string:username>')
api.add_resource(Login, '/login')


@app.route('/')
def render_vue():
    return render_template("index.html")
