from flask import Flask
app = Flask(__name__)
app.config['DEBUG'] = True


@app.route('/')
def hello():
    return 'Hello World!'

@app.errorhandler(404)
def page_not_found(e):
    return 'Sorry, nothing at this URL.', 404
