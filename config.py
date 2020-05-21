from decouple import config
class Config:
    pass

class ProductionConfig(Config):
    DEBUG = False
    print("ITS CONFIG")
    SQLALCHEMY_DATABASE_URI = config('DATABASE_URL', default='localhost')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    STATIC_FOLDER = "/static"
    TEMPLATE_FOLDER = "/templatess"
    SECRET_KEY = config('SECRET_KEY', default='localhost')

class DevelopmentConfig(Config):
    DEBUG = True
    print("ITS DEVELOPMENT")
    SQLALCHEMY_DATABASE_URI = 'sqlite:///data.db'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    STATIC_FOLDER = "../P2/front-end/dist/static"
    TEMPLATE_FOLDER = "../P2/front-end/dist"
    SECRET_KEY = "M2CjZ52aDgtvt3XXRWBktEPY64EqtxhJ"

config = {
    'development': DevelopmentConfig,
    'production': ProductionConfig
}