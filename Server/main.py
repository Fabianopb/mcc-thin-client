from flask import Flask
from flask import request
from flask import g
from flask import jsonify
from flask_httpauth import HTTPBasicAuth
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)

auth = HTTPBasicAuth()

app = Flask(__name__)
app.config['DEBUG'] = True

users = {
    "test": "secret",
    "newuser": "securePassword"
}

SECRET_KEY='thisAppIsAwesome:)'

@auth.verify_password
def verify_password(user_token, password):
    user = verify_auth_token(user_token)
    if user: # User from token
        g.user = user
        return True
    else:
        if user_token in users:
            if password == users.get(user_token):
                g.user=user_token
                return True

    return False

def generate_auth_token(user, expiration = 1200): #1200~20minutes
    #s = Serializer(app.config['SECRET_KEY'], expires_in = expiration)
    s = Serializer(SECRET_KEY, expires_in = expiration)
    #print('dumps:' + str(s.dumps({ 'user': user })))
    return s.dumps({ 'id': user })
    #return s.dumps('abcd')

def verify_auth_token(token):
    s = Serializer(SECRET_KEY)
    try:
        data = s.loads(token)
    except SignatureExpired:
        return None # valid token, but expired
    except BadSignature:
        return None # invalid token

    if data['id'] in users:
        return data['id']
    else:
        return None


@app.route('/token/')
@auth.login_required
def get_token():
    print("Token for user: " + str(g.user))
    token = generate_auth_token(g.user)
    return jsonify({ 'token': token.decode('ascii') })

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

@app.route('/start/')
@auth.login_required
def start():
    return "You are successfully authenticated, start!"


@app.route('/auth/')
@auth.login_required
def auth():
    return "You are successfully authenticated!"


@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
