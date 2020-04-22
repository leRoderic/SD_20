from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask

db = SQLAlchemy()


def create_app():
    app = Flask(__name__,
                static_folder="../front-end/dist/static",
                template_folder="../front-end/dist")
    app.config.from_object(__name__)
    CORS(app, resources={r'/*': {'origins': '*'}})
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    db.init_app(app)
    return app
