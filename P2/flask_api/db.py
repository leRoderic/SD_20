from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask

db = SQLAlchemy()
secret_key = "M2CjZ52aDgtvt3XXRWBktEPY64EqtxhJ"


def create_app():
    app = Flask(__name__,
                static_folder="../front-end/dist/static",
                template_folder="../front-end/dist")
    app.config.from_object(__name__)
    CORS(app, resources={r'/*': {'origins': '*'}})
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['SECRET_KEY'] = secret_key
    db = SQLAlchemy(app)
    return app
