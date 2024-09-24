import cv2
import matplotlib.pyplot as plt

# Load the image
img = cv2.imread(r"C:\Users\Dell\repos\FinalProject\opencv\waterbottle.jpg")
image = img[54:328, :]

if image is None:
    print("Error: Image not loaded properly")
else:
    # Convert to grayscale
    grayimg = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Define the range of gray levels to keep
    lower_bound = 210
    upper_bound = 220

    # Apply in-range thresholding
    binary = cv2.inRange(grayimg, lower_bound, upper_bound)

    # Display the binary image
    # plt.imshow(binary, cmap='gray')
    # plt.title("In-Range Thresholded Image")
    # plt.axis('on')
    # plt.show()

    # Find contours
    contours, _ = cv2.findContours(binary, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # Draw all contours (for visualization)
    contour_img = image.copy()
    cv2.drawContours(contour_img, contours, -1, (0, 255, 0), 2)

    # Display the contours
    # plt.imshow(cv2.cvtColor(contour_img, cv2.COLOR_BGR2RGB))
    # plt.title("Contours")
    # plt.axis('on')
    # plt.show()

    # Assuming the largest contour corresponds to the water level
    largest_contour = max(contours, key=cv2.contourArea)

    # Get the bounding box of the largest contour
    x, y, w, h = cv2.boundingRect(largest_contour)

    # Draw the bounding box (for visualization)
    cv2.rectangle(image, (x, y), (x + w, y + h), (255, 0, 0), 2)

    # Display the bounding box
    # plt.imshow(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))
    # plt.title("Bounding Box")
    # plt.axis('on')
    # plt.show()

    # Calculate the fullness percentage
    bottle_height = image.shape[0]
    water_level_height = h
    fullness_percentage = water_level_height / bottle_height * 100

    # Round the fullness percentage to two decimal places
    fullness_percentage_rounded = round(fullness_percentage, 2)

    print("Fullness percentage: " + str(fullness_percentage_rounded) + "%")

