import os
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
import numpy as np
import tensorflow as tf
from tensorflow.keras.preprocessing import image
from pathlib import Path

imgToProcess = "9.jpg"

# Load the trained model
model_path = Path('fine_tuned_flood_detection_model.keras')
assert model_path.exists(), f"Model file does not exist: {model_path}"
model = tf.keras.models.load_model(model_path)

# Define labels for the classes
labels = ['Flooding', 'No Flooding']

# Prepare image for mobilenet prediction
def preprocess_image(file):
    img_path = 'evaluate/'
    img = image.load_img(img_path + file, target_size=(224, 224))
    img_array = image.img_to_array(img)
    img_array_expanded_dims = np.expand_dims(img_array, axis=0)
    return tf.keras.applications.mobilenet.preprocess_input(img_array_expanded_dims)

# Display image which we want to predict (optional for non-notebook environments)
# from IPython.display import Image
# Image(filename='evaluate/1.jpg', width=300, height=200)

# Preprocess the image and make prediction
preprocessed_image = preprocess_image(imgToProcess)
predictions = model.predict(preprocessed_image)

# Print predicted accuracy scores for both classes
print(f"Predictions: {predictions}")

# Get the maximum probability score for predicted class
result = np.argmax(predictions)

# Print the predicted class label
print(f"Predicted Class: {labels[result]}")
