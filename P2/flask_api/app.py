from flask import *
from data import events, artists

app = Flask(__name__)


@app.route('/')
def hello_world():
    return 'Hello World!'


@app.route('/artists', methods=['GET'])
def get_artists():
    return jsonify({'artists': artists})


@app.route('/events', methods=['GET'])
def get_events():
    return jsonify({'events': events})


if __name__ == '__main__':
    app.run(port=5000, debug=True)
