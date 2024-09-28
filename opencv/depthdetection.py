import cv2

# RTMP URL
rtmp_url = "rtmp://192.168.0.109:1935/live/stream"

# Create a VideoCapture object
cap = cv2.VideoCapture(rtmp_url)

if not cap.isOpened():
    print("Error: Could not open video stream.")
    exit()

while True:
    ret, frame = cap.read()
    if not ret:
        print("Error: Could not read frame.")
        break

    # Display the frame
    cv2.imshow('RTMP Stream', frame)

    # Press 'q' to exit the video display
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the VideoCapture object and close display window
cap.release()
cv2.destroyAllWindows()
