import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image
from flask import Flask, request, jsonify
from pathlib import Path
import uuid

app = Flask(__name__)

# Load the trained model
model_path = Path('fine_tuned_flood_detection_model.keras')
assert model_path.exists(), f"Model file does not exist: {model_path}"
model = tf.keras.models.load_model(model_path)

# Define labels for the classes
labels = ['Flooding', 'No Flooding']

def preprocess_image(img_path):
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

@app.route('/')
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
        
        try:
            os.remove(temp_path)
        except:
            pass
            
        return jsonify({"floodstatus": labels[result]})
            
    except Exception:
        if 'temp_path' in locals():
            try:
                os.remove(temp_path)
            except:
                pass
        return jsonify({"floodstatus": ""})

if __name__ == '__main__':
    app.run(debug=True)