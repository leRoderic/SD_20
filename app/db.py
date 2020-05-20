from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask

db = SQLAlchemy()


def create_app():
    app = Flask(__name__)
    envirnoment = config['development']
    if config_decouple('PRODUCTION', default=False):
        enviroment = config['production']
    app.config.from_object(environment)
    db = SQLAlchemy(app)
    return app
