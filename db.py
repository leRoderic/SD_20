import os
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask
from decouple import config as config_decouple
from config import config, Config

db = SQLAlchemy()


def create_app():
    app = Flask(__name__)
    environment = Config()
    '''
    environment = config['development']
    if os.environ.get('PRODUCTION'):#config_decouple('PRODUCTION'):
        environment = config['production']
    app.config.from_object(environment)
    '''
    db = SQLAlchemy(app)
    return app
