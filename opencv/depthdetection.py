import cv2
import threading
from http.server import BaseHTTPRequestHandler, HTTPServer

# RTMP URL
rtmp_url = "rtmp://192.168.0.109:1935/live/stream"

video_receiving = False

class FlagHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/plain")
        self.end_headers()
        self.wfile.write(str(video_receiving).encode())

def run_server():
    server_address = ("", 8000)
    httpd = HTTPServer(server_address, FlagHandler)
    print("Server running on port 8000")
    httpd.serve_forever()

def check_video_stream():
    global video_receiving
    cap = cv2.VideoCapture(rtmp_url)
    
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Error: Could not read frame.")
            video_receiving = False
            cap.release()
            cap = cv2.VideoCapture(rtmp_url)  # Try to reconnect
        else:
            video_receiving = True
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