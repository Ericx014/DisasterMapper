import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image
from flask import Flask, request, jsonify
from pathlib import Path
import uuid
import firebase_admin
from firebase_admin import credentials, db
from datetime import datetime 

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

# Load the trained model
model_path = Path('fine_tuned_flood_detection_model.keras')
assert model_path.exists(), f"Model file does not exist: {model_path}"
model = tf.keras.models.load_model(model_path)
labels = ['Flooding', 'No Flooding']

def preprocess_image(img_path):
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

@app.route('/', methods=['GET'])
def get_data():
    return jsonify({"floodstatus": ""})

@app.route('/detect', methods=['POST'])
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
        
        current_timestamp = datetime.now().isoformat()
        
        flood_data = {
            'status': flood_status,
            'timestamp': current_timestamp
        }

        ref = db.reference('/flood_status')
				# ref.set(flood_data)
        ref.push(flood_data)
        print(f"Detected flood status: {flood_status}")
        return jsonify({"floodstatus": flood_status})
            
    except Exception:
        if 'temp_path' in locals():
            try:
                os.remove(temp_path)
            except:
                pass
        return jsonify({"floodstatus": ""})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)