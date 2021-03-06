#!/usr/bin/env python3

from flask import Flask
from flask import request
from flask import g
from flask import jsonify
from flask_httpauth import HTTPBasicAuth
from itsdangerous import (TimedJSONWebSignatureSerializer as Serializer, BadSignature, SignatureExpired)
from libcloud.compute.types import Provider
from libcloud.compute.providers import get_driver
import json
import subprocess
import hashlib

auth = HTTPBasicAuth()

app = Flask(__name__)
app.config['DEBUG'] = True

users = {
    "test": "bd2b1aaf7ef4f09be9f52ce2d8d599674d81aa9d6a4421696dc4d93dd0619d682ce56b4d64a9ef097761ced99e0f67265b5f76085e5b0ee7ca4696b2ad6fe2b2", # passwd=secret
    "newuser": "857b95a7e57b32249f27d1e425fbadab6b0d51adee4e251c50d7c6b5d137d775b7d053dacf778ffb19faf840ee20d511d7450262602d4e18d539addcf9c847cb" # passwd=securePassword
}

apps = {"apps": [
    {
        "instanceName": "openoffice",
        "readableName": "OpenOffice"
    },
    {
        "instanceName": "inkscape",
        "readableName": "Inkscape"
    }
]}
SECRET_KEY = 'thisAppIsAwesome:)'
PASSWD_STRING = 'passwordString'
TOKEN_EXPIRATION = 3600  # 60 minutes

# Initialize libcloud Google Compute Engine Driver using service account authorization
ComputeEngine = get_driver(Provider.GCE)
gce = ComputeEngine('860271242030-compute@developer.gserviceaccount.com', 'key/mcc-2016-g13-p1-290f94a963cb.json',
                    datacenter='europe-west1-d', project='mcc-2016-g13-p1')

running_node = None
running_node_name = None
heartbeat_process = None


@auth.verify_password
def verify_password(user_token, password):
    user = verify_auth_token(user_token)
    if user:  # User from token
        g.user = user
        return True
    else:
        if user_token in users:
            if password == users.get(user_token):
            #if password == hashlib.sha512(users.get(user_token) + PASSWD_STRING).hexdigest():
                g.user = user_token
                return True

    return False


def generate_auth_token(user, expiration=TOKEN_EXPIRATION):  # 1200~20minutes
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
    print("Listing apps")
    return json.dumps(apps)


@app.route('/heartbeat/')
@auth.login_required
def heartbeat():
    print("Heartbeat")
    global heartbeat_process
    if heartbeat_process is None:
        print("No heartbeat process running")
        return 'False'

    # restart heartbeat script
    heartbeat_process.kill()
    heartbeat_process = subprocess.Popen(["python", "heartbeat.py", str(running_node_name)])

    token = generate_auth_token(g.user)
    return jsonify({'token': token.decode('ascii')})


@app.route('/start/', methods=['POST'])
@auth.login_required
def start():
    app = request.form.get('app')
    if app is None:
        return 'False: No application'
    print("Starting " + str(app))

    global running_node_name

    if app == 'inkscape':
        running_node_name = 'tt-inkscape-1'
    elif app == 'openoffice':
        running_node_name = 'tt-openoffice-1'
    else:
        return 'False: Wrong application'

    node = gce.ex_get_node(running_node_name)
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
            return 'False: IndexError'

        # VM has started, respond with IP and start the heartbeat process
        global heartbeat_process
        heartbeat_process = subprocess.Popen(["python", "heartbeat.py", running_node_name])

        print("Started")

        return ip[0][0].public_ips[0]

    return 'False: Not starting '


@app.route('/stop/')
@auth.login_required
def stop():
    global running_node
    global running_node_name
    global heartbeat_process
    if running_node is None:
        return 'False'

    print("Stopping " + running_node_name)

    gce.ex_stop_node(running_node)

    heartbeat_process.kill()

    running_node = None
    running_node_name = None
    heartbeat_process = None
    print("Stopped")
    return 'True'


@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404


# TODO: NOT USED VVVVV
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
