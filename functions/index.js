//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.createUpdate = functions.firestore
  .document('updates/{documentId}')
  .onCreate(event => {
    // Get an object representing the document
    // e.g. {'name': 'Marie', 'age': 66}
    var newValue = event.data.data();

    // access a particular field as you would any JS property
    //var name = newValue.name;

    // perform desired operations ...
	
	// Create a notification
    const payload = {
        notification: {
            title:newValue.name,
            body: newValue.text,
            sound: "default"
        },
    };

  //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("generalUpdates", payload, options);
});