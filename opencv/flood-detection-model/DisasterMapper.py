import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image
from flask import Flask, request, jsonify
from flask_socketio import SocketIO
from pathlib import Path
import uuid
# from flask_cors import CORS
import firebase_admin
from firebase_admin import credentials, db

try:
    # Specify the path to your service account key JSON file
    service_account_path = "./disastermapperchat-firebase-adminsdk-ft6jn-c726072695.json" # Replace with actual path
    print(f"Loading service account credentials from: {service_account_path}")
    if not Path(service_account_path).exists():
        raise FileNotFoundError(f"Service account file not found at: {service_account_path}")

    # Initialize Firebase Admin SDK with service account credentials
    cred = credentials.Certificate(service_account_path)
    firebase_admin.initialize_app(cred, {
        'databaseURL': "https://disastermapperchat-default-rtdb.asia-southeast1.firebasedatabase.app/"  # Replace with your Firebase Realtime Database URL
    })
    
except Exception as e:
    print(f"Error initializing Firebase Admin SDK: {str(e)}")
    raise  

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*", allow_origin="http://*")

# Load the trained model
model_path = Path('fine_tuned_flood_detection_model.keras')
assert model_path.exists(), f"Model file does not exist: {model_path}"
model = tf.keras.models.load_model(model_path)
labels = ['Flooding', 'No Flooding']

@socketio.on('connect')
def handle_connect():
    print("Client connected")
    socketio.emit('connection_response', {'data': 'Connected successfully!'})

@socketio.on('disconnect')
def handle_disconnect():
    print("Client disconnected")

@socketio.on_error()
def handle_error(e):
    print(f"SocketIO error: {e}")

def preprocess_image(img_path):
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

@app.route('/', methods=['GET'])
def get_data():
    return jsonify({"floodstatus": ""})

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({"floodstatus": ""})
    
    img_file = request.files['file']
    
    if img_file.filename == '':
        return jsonify({"floodstatus": ""})
    
    try:
        temp_filename = f"temp_{uuid.uuid4().hex}.jpg"
        temp_path = os.path.join(os.getcwd(), temp_filename)
        
        img_file.save(temp_path)
        preprocessed_image = preprocess_image(temp_path)
        predictions = model.predict(preprocessed_image)
        result = np.argmax(predictions, axis=1)[0]
        flood_status = labels[result]
        
        try:
            os.remove(temp_path)
        except:
            pass
        
        # Emit the flood status to all connected clients
        socketio.emit('flood_status', {'status': flood_status})
        print(f"Emitted flood status: {flood_status}")
        
        ref = db.reference('/flood_status')
        ref.set(flood_status)
        print(f"Predicted/Detected status: {flood_status}")
        return jsonify({"floodstatus": flood_status})
            
    except Exception:
        if 'temp_path' in locals():
            try:
                os.remove(temp_path)
            except:
                pass
        return jsonify({"floodstatus": ""})

if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8000, debug=True)