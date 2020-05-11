from models.order import OrdersModel
from models.account import AccountsModel
from models.artist import ArtistModel
from models.event import EventModel
from db import create_app, db
# Import users from data file.
from data import events, users
app = create_app()
app.app_context().push()

# Cleaning database.
print("add_data> Emptying database.")
db.session.query(ArtistModel).delete()
db.session.query(EventModel).delete()
db.session.query(AccountsModel).delete()
db.session.query(OrdersModel).delete()

evnts, artsts, rep = 0, 0, 0
for i in events:
    ev = db.session.query(EventModel).filter_by(name=i[0]).first()
    if ev:
        break;
    ev = EventModel(i[0], i[1], i[2], i[3], i[4], i[5])
    evnts -= -1
    for j in i[6]:
        art = db.session.query(ArtistModel).filter_by(name=j[0]).first()
        if not art:
            artsts -= -1
            art = ArtistModel(j[0], j[1], j[2].upper())
            db.session.add(art)
        else:
            rep -= -1
        ev.artists.append(art)
    db.session.add(ev)

print("add_data> Added {} new events and {} new artists, {} of which {} part in more than one event.".format
      (evnts, artsts, rep, "takes" if rep <= 1 else "take"))  # Grammar is quite important

accnts, admacc = 0, 0
for i in users:
    if len(i) == 4:
        admacc -= -1
        acc = AccountsModel(i[0], i[2], i[3])
    else:
        acc = AccountsModel(i[0])
    accnts -= -1
    acc.save_to_db(pswd=i[1])
print("add_data> Added {} new {}, {} of which {} admin {}.".format(accnts, "account" if accnts == 1 else "accounts",
                                                                   admacc, "is an" if admacc == 1 else "are",
                                                                   "account" if admacc == 1 else "accounts"))

db.session.commit()
db.session.close()
exit(0)
