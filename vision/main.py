import os
import sys

from flask import Flask, redirect, render_template, request

from google.cloud import firestore
from google.cloud import storage
from google.cloud import vision


app = Flask(__name__)


@app.route('/')
def homepage():
    # Create a Cloud Firestore client.
    firestore_client = firestore.Client()

    # Use the Cloud Firestore client to fetch information from Cloud Firestore about
    # each photo.
    photo_documents = list(firestore_client.collection(u'photos').get())

    # Return a Jinja2 HTML template.
    return render_template('homepage.html', photo_documents=photo_documents)

@app.route('/upload_photo', methods=['GET', 'POST'])
def upload_photo():
    # Create a Cloud Storage client.
    storage_client = storage.Client()

    # Get the Cloud Storage bucket that the file will be uploaded to.
    bucket = storage_client.get_bucket(os.environ.get('CLOUD_STORAGE_BUCKET'))

    # Create a new blob and upload the file's content to Cloud Storage.
    photo = request.files['file']
    blob = bucket.blob(photo.filename)
    blob.upload_from_string(
            photo.read(), content_type=photo.content_type)

    # Make the blob publicly viewable.
    blob.make_public()
    image_public_url = blob.public_url
    
    # Create a Cloud Vision client.
    vision_client = vision.ImageAnnotatorClient()

    # Retrieve a Vision API response for the photo stored in Cloud Storage
    image = vision.types.Image()
    image.source.image_uri = 'gs://{}/{}'.format(os.environ.get('CLOUD_STORAGE_BUCKET'), blob.name)

    # Return the best text comparision
    # Add entry for Text annotation
    response = vision_client.text_detection(image=image)
    output = response.text_annotations
    texts = []
    buffer = ""
    
    legal_food = {}
    import json
    with open('../../compounds.json', encoding='utf-8') as f:
        print('Opened DB')
        legal_food = json.load(f)
    print('Closed DB')
 #   print(legal_food)
    isStart = False
    print('Texts:')
    for text in output:
        print(text.description, (text.description == text.description.upper()) and (text.description.find(':') >= 0))
        print("isStart:", isStart)
        if not (text.description == text.description.upper()):
            continue
        if (text.description == text.description.upper()) and (text.description.find(':') >= 0) and (not isStart):
            isStart = True
            continue
        if (isStart) and (text.description.find(',') >= 0):
            buffer = buffer + " " + text.description
            texts.append(buffer)
            buffer = ""
            continue
        elif (isStart):
            buffer = buffer + " " + text.description
        if (text.description.find('.') >= 0) and (isStart):
            texts.append(buffer)
            break

    # Parse it to start the text from "INGRE"-DIENTS" (with auto correct)
    # end it at the next period and no caps next to it

    # Create a Cloud Firestore client
    firestore_client = firestore.Client()

    # Get a reference to the document we will upload to
    doc_ref = firestore_client.collection(u'photos').document(blob.name)
    """
    # Note: If we are using Python version 2, we need to convert
    # our image URL to unicode to save it to Cloud Firestore properly.
    if sys.version_info < (3, 0):
        image_public_url = unicode(image_public_url, "utf-8")
    """
    # Construct key/value pairs with data
    data = {
        u'image_public_url': image_public_url,
        u'top_label': texts[0]
    }

    # Set the document with the data
    doc_ref.set(data)

    # Redirect to the home page. Add parameter for Text recognition
    return render_template('homepage.html', texts=texts, image_public_url=image_public_url)


@app.errorhandler(500)
def server_error(e):
    return """
    An internal error occurred: <pre>{}</pre>
    See logs for full stacktrace.
    """.format(e), 500


if __name__ == '__main__':
    # This is used when running locally. Gunicorn is used to run the
    # application on Google App Engine. See entrypoint in app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)