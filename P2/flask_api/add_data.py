from flask_sqlalchemy import SQLAlchemy
from models.artist import ArtistModel
from models.event import EventModel
from db import create_app

app = create_app()
app.app_context().push()
db = SQLAlchemy(app)

data = [["Lovin Ibiza Festival", "Amnesia Ibiza", "Ibiza", "1-05-2020", "20", "1500",
         [["Marco Faraone", "Italy", "electronic"], ["Danny Serrano", "Spain", "trap"], ["Catz 'n Dogz", "Poland", "electronic"]]],
        ["Warm Up Festival 2020", "La fica", "Murcia", "1-10-2020", "42", "80000",
         [["Leiva", "Spain", "pop"], ["Anni B Sweet", "Spain", "indie"],["Kraftwek", "Germany", "electronic"]],
         ["Hot Chip", "England", "electronic"], ["Jhonny Marr", "England", "indie"], ["Dorian", "Spain", "indie"],
         ["Carlos Sadness", "Spain", "indie"], ["Mando Diao", "Sweeden", "indie"], ["Viva Suecia", "Spain", "indie"],
         ["León Benavente", "Spain", "indie"], ["Rufus T.", "Spain", "rock"]],
        ["Mallorca Live Festival 2020", "Calvià", "Mallorca", "8-10-2020", "82", "15000",
         [["Michael Kiwanuka", "England", "trap"], ["Miles Kane", "England", "indie"], ["Kate Tempest", "England", "hip hop"],
          ["Carolina Durante", "Spain", "rock"], ["Delaossa", "Spain", "hip hop"], ["Nathy Peluso", "Argentina", "reggae"],
          ["Leiva", "Spain", "pop"], ["Crystal Fighters", "Spain", "indie"], ["Pet Shop Boys", "England", "electronic"],
          ["Miss Caffeina", "Spain", "indie"], ["Shinova", "Spain", "rock"], ["Monarchy", "England", "pop"]]],
        ["Festival Tomavistas 2020", "Parque Enrique Tierno Galván", "Madrid", "3-09-2020", "100", "10000",
         [["Jarvis Cocker", "England", "pop"], ["Novedades Carminha", "Spain", "rock"], ["Allah-Las", "USA", "rock"],
          ["Cate Le Bon", "Wales", "indie"], ["Maika Makovski", "Spain", "pop"], ["León Benavente", "Spain", "indie"],
          ["El Columpio Asesino", "Spain", "rock"], ["Boy Pablo", "Norway", "indie"], ["Austra", "Canada", "electronic"],
          ["Mikal Cronin", "USA", "rock"], ["of Montreal", "USA", "rock"], ["Rolling Blackouts C.F", "Australia", "rock"],
          ["Axolotes Mexicanos", "Spain", "indie"], ["Dorian", "Spain", "indie"], ["The Horrors", "England", "rock"],
          ["Amaia", "Spain", "indie"], ["Anna Calvi", "England", "indie"], ["Nadia Rose", "England", "other"],
          ["Jessy Lanzan", "Canada", "electronic"], ["Bigott", "Spain", "indie"], ["Chloral", "Spain", "rock"]]],
        ["Primavera Sound 2020", "Oporto", "Portugal", "26-08-2020", "110", "10000",
         [["Lana Del Rey", "USA", "pop"], ["Tyler, the Creator", "USA", "hip hop"], ["Yo La Tengo", "USA", "indie"],
          ["Chromatics", "USA", "electronic"], ["Pavement", "USA", "indie"], ["Mavis Staples", "USA", "rock"],
          ["Les Savy Fav", "USA", "rock"], ["Kim Gordon", "USA", "rock"], ["Paloma Mami", "Chile", "reggae"],
          ["Les Amazones d'Afrique", "Mali", "other"], ["Núria Graham", "Spain", "indie"], ["La Favi", "Spain", "reggae"],
          ["Die Katapult", "Spain", "electronic"], ["Lorena Álvarez", "Spain", "indie"]]],
        ["Madrid Puro Reggaeton Festival", "Ifema", "Madrid", "26-06-2020", "50", "7500",
         [["Bad Bunny", "Puerto Rico", "reggae"], ["Daddy Yankee", "Puerto Rico", "reggae"],
          ["Maikel Delacalle", "Spain", "reggae"], ["Natti Natasha", "Republica Dominicana", "reggae"]]]]

# Delete database content in order to test the script.
db.session.query(ArtistModel).delete()
db.session.query(EventModel).delete()

evnts, artsts, rep = 0, 0, 0

for i in data:

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

db.session.commit()
db.session.close()
print("add_data> Added {} new events and {} new artists, {} of which {} part in more than one event.".format
      (evnts, artsts, rep, "takes" if rep <= 1 else "take")) # Grammar is quite important
exit(0)