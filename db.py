from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask
from config import config

db = SQLAlchemy()


def create_app():
    app = Flask(__name__)
    environment = config['development']
    if config_decouple('PRODUCTION', default=False):
        environment = config['production']
    app.config.from_object(environment)
    db = SQLAlchemy(app)
    return app