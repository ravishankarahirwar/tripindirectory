//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

//general updates PRODUCTION function
exports.createUpdate = functions.firestore
  .document('updates/{documentId}')
  .onCreate(event => {
    var newValue = event.data.data();
	
	// Create a DATA notification
    const payload = {
       data: {
        type: newValue.mType,
        docId: newValue.mTimeStamp
      }
    };
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };
    return admin.messaging().sendToTopic("generalUpdates", payload, options);
});

//general updates TESTING function
exports.createUpdate = functions.firestore
  .document('updatestest/{documentId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: newValue.mType,
        docId: newValue.mTimeStamp
      }
    };
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("generalUpdatesTest", payload, options);
});
