from flask import Flask
from flask import request
app = Flask(__name__)
app.config['DEBUG'] = True


@app.route('/')
def hello():
    return 'Hello World!'


@app.route('/form/', methods=['POST'])
def form():
    user = request.form.get('user')
    password = request.form.get('password')
    if user == 'test' and password == 'secret':
        return 'Authenticated'
    else:
        return 'Wrong credentials'

@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
