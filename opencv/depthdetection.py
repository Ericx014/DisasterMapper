import cv2
import numpy as np
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer
import json

rtmp_url = "rtmp://192.168.0.109:1935/live/stream"

video_receiving = False
flood_level = 0

class DataHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "application/json")
        self.end_headers()
        data = {
            "video_receiving": video_receiving,
            "flood_level": flood_level
        }
        self.wfile.write(json.dumps(data).encode())

def run_server():
    server_address = ("", 8000)
    httpd = HTTPServer(server_address, DataHandler)
    print("Server running on port 8000")
    httpd.serve_forever()

def detect_flood_level(frame):
    # Convert frame to HSV color space
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    
    # Define range of blue color in HSV
    lower_blue = np.array([100, 50, 50])
    upper_blue = np.array([140, 255, 255])
    
    # Threshold the HSV image to get only blue colors
    mask = cv2.inRange(hsv, lower_blue, upper_blue)
    
    # Calculate the percentage of blue pixels
    blue_percentage = (cv2.countNonZero(mask) / (frame.shape[0] * frame.shape[1])) * 100
    
    # Map the percentage to a flood level (0-3)
    if blue_percentage < 10:
        return 0  # No flood
    elif blue_percentage < 30:
        return 1  # Low flood risk
    elif blue_percentage < 50:
        return 2  # Medium flood risk
    else:
        return 3  # High flood risk

def check_video_stream():
    global video_receiving, flood_level
    cap = cv2.VideoCapture(rtmp_url)
    
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Could not read frame.")
            video_receiving = False
            flood_level = 0
            cap.release()
            cap = cv2.VideoCapture(rtmp_url)  # Try to reconnect
        else:
            video_receiving = True
            flood_level = detect_flood_level(frame)
            cv2.imshow('RTMP Stream', frame)
        
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

# Start the HTTP server in a separate thread
threading.Thread(target=run_server, daemon=True).start()

# Start the video stream check in the main thread
check_video_stream()

print("Shutting down...")