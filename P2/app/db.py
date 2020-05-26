from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from flask import Flask

db = SQLAlchemy()
secret_key = "M2CjZ52aDgtvt3XXRWBktEPY64EqtxhJ"


def create_app():
    app = Flask(__name__)
    environment = config['development']
    if config_decouple('PRODUCTION', cast=bool, default=False):
        environment = config['production']
    app.config.from_object(environment)
    db = SQLAlchemy(app)
    return app
