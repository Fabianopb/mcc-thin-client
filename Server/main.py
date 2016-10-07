from flask import Flask
from flask import request
app = Flask(__name__)
app.config['DEBUG'] = True


@app.route('/')
def hello():
    return 'Hello World!'

@app.route('/form/')
def form():
    return request.query_string

@app.route('/form2/')
def form():
    return request.url

@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
