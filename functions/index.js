const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendGroupChatNotification = functions.database
    .ref("/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
        const message = snapshot.val();
        if (!message) {
            console.error("No message data available.");
            return null;
        }

        const notification = {
            notification: {
                title: `${message.senderUsername || "Anonymous"} says:`,
                body: message.content,
            },
            topic: "group_chat",
        };

        try {
            const response = await admin.messaging().send(notification);
            console.log("Notification sent successfully:", response);
        } catch (error) {
            console.error("Error sending notification:", error);
        }

        return null;
    });
