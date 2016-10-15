from flask import Flask
from flask import request
from flask.ext.httpauth import HTTPBasicAuth

auth = HTTPBasicAuth()

app = Flask(__name__)
app.config['DEBUG'] = True


@auth.verify_password
def verify_password():
    if user == 'test' and password == 'secret':
        return True
    else
        return False

@app.route('/')
def hello():
    return 'Welcome to the backend!'


@app.route('/form/', methods=['POST'])
def form():
    user = request.form.get('user')
    password = request.form.get('password')
    if user == 'test' and password == 'secret':
        return 'Authenticated'
    else:
        return 'Wrong credentials'

@app.route('/auth/')
@auth.login_required
def auth():
    return "You are successfully authenticated!"


@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
