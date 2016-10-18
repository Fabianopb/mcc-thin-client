from flask import Flask
from flask import request
from flask import g
from flask import jsonify
from flask_httpauth import HTTPBasicAuth
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from libcloud.compute.types import Provider
from libcloud.compute.providers import get_driver

auth = HTTPBasicAuth()

app = Flask(__name__)
app.config['DEBUG'] = True

users = {
    "test": "secret",
    "newuser": "securePassword"
}

applications = {
    "1": "Inkscape",
    "2": "OpenOffice"
}

SECRET_KEY = 'thisAppIsAwesome:)'

# Initialize libcloud Google Compute Engine Driver using service account authorization
ComputeEngine = get_driver(Provider.GCE)
gce = ComputeEngine('860271242030-compute@developer.gserviceaccount.com', 'key/mcc-2016-g13-p1-290f94a963cb.json',
                    datacenter='europe-west1-d', project='mcc-2016-g13-p1')

running_node = None


@auth.verify_password
def verify_password(user_token, password):
    user = verify_auth_token(user_token)
    if user:  # User from token
        g.user = user
        return True
    else:
        if user_token in users:
            if password == users.get(user_token):
                g.user = user_token
                return True

    return False


def generate_auth_token(user, expiration=1200):  # 1200~20minutes
    # s = Serializer(app.config['SECRET_KEY'], expires_in = expiration)
    s = Serializer(SECRET_KEY, expires_in=expiration)
    # print('dumps:' + str(s.dumps({ 'user': user })))
    return s.dumps({'id': user})
    # return s.dumps('abcd')


def verify_auth_token(token):
    s = Serializer(SECRET_KEY)
    try:
        data = s.loads(token)
    except SignatureExpired:
        return None  # valid token, but expired
    except BadSignature:
        return None  # invalid token

    if data['id'] in users:
        return data['id']
    else:
        return None


@app.route('/token/')
@auth.login_required
def get_token():
    print("Token for user: " + str(g.user))
    token = generate_auth_token(g.user)
    return jsonify({'token': token.decode('ascii')})


@app.route('/getapps/')
@auth.login_required
def get_apps():
    return jsonify(**applications)


# TODO: Not used at the moment
@app.route('/isrunning/', methods=['POST'])
@auth.login_required
def is_running():
    isRunning = False  # TODO: check machine
    return isRunning


@app.route('/start/', methods=['POST'])
@auth.login_required
def start():
    app = request.form.get('app')
    if app == '1':
        node = gce.ex_get_node('tt-inkscape-1')
    elif app == '2':
        node = gce.ex_get_node('tt-openoffice-1')
    else:
        return 'False'

    result = gce.ex_start_node(node)
    if result:
        nodes = [node]
        ip = gce.wait_until_running(nodes, wait_period=2, timeout=30)
        global running_node
        running_node = node
        # Check if instance has a public ip
        try:
            ip[0][1]
        except IndexError:
            return 'False'

        # VM has started, respond with IP
        return ip[0][0].public_ips[0]

    return 'False'
    # TODO: Should the client remember what app it was opening (either IP or some identificator to know what to ask in "isrunning"?


@app.route('/stop/', methods=['POST'])
@auth.login_required
def stop():
    if running_node is None:
        return 'False'

    #vm_id = request.form.get('vm_id')
    #save = request.form.get('save')  # Should it save state?

    gce.ex_stop_node(running_node)

    global running_node
    running_node = None
    return 'True'


# NOT USED VVVVV
@app.route('/auth/')
@auth.login_required
def auth():
    return "You are successfully authenticated!"


@app.route('/form/', methods=['POST'])
def form():
    user = request.form.get('user')
    password = request.form.get('password')
    if user == 'test' and password == 'secret':
        return 'Authenticated'
    else:
        return 'Wrong credentials'


@app.route('/')
def hello():
    return 'Welcome to the backend!'


@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
