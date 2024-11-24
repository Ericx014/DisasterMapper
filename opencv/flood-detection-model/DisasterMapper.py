import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image
from flask import Flask, request, jsonify
from pathlib import Path

#/

app = Flask(__name__)

# Load the trained model
model_path = Path('fine_tuned_flood_detection_model.keras')
assert model_path.exists(), f"Model file does not exist: {model_path}"
model = tf.keras.models.load_model(model_path)

# Define labels for the classes
labels = ['Flooding', 'No Flooding']

# Prepare image for mobilenet prediction
def preprocess_image(file):
    img_path = 'evaluate/' + file
    img = image.load_img(img_path, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

@app.route('/')
def get_data():
    # This returns a sample data in JSON format
    data = {"key": "value"}
    return jsonify(data)

@app.route('/predict', methods=['POST'])
def predict():
    if 'file' not in request.files:
        return jsonify({'error': 'No file part'})

    img_file = request.files['file']
    
    if img_file.filename == '':
        return jsonify({'error': 'No selected file'})
    
    # Save the uploaded file temporarily
    img_path = 'temp_image.jpeg'
    img_file.save(img_path)
    
    # Preprocess the image and make prediction
    preprocessed_image = preprocess_image(img_path)
    predictions = model.predict(preprocessed_image)

    # Get the maximum probability score for the predicted class
    result = np.argmax(predictions, axis=1)
    predicted_class = labels[result[0]]  # Get the label based on the prediction index

    return jsonify({"floodstatus": predicted_class})

if __name__ == '__main__':
    app.run(debug=True)
