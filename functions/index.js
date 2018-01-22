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
exports.createUpdateTest = functions.firestore
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

//Make the initial mAccountStatusValue 0
exports.emptyCompanyCreated = functions.firestore
  .document('partners/{documentId}')
  .onCreate(event => {
      return event.data.ref.update({ mAccountStatus: 0 });
});


exports.newVerifiedCompany = functions.database.ref('/userCategories/companyVerifiedUsers/{pushId}')
    .onCreate(event => {
      // Grab the current value of what was written to the Realtime Database.
      var newValue = event.data.val();
	  
	  if(newValue.mFcmToken !=null){
		   console.log('Sending Verified Notification', event.params.pushId, newValue);
	  // Create a DATA notification
    const payload = {
       data: {
        type: "2",
        mCompanyName: newValue.mCompanyName,
      }
    };
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


     return admin.messaging().sendToDevice(newValue.mFcmToken, payload,options).then((response)=> {
        console.info("Successfully sent notification")
      }).catch(function(error) {
        console.warn("Error sending notification " , error)
         });
	  }else{
		  		   console.log('Sending No Verified Notification', event.params.pushId, newValue);
	  }
     
     
    });
	
	exports.newRejectedCompany = functions.database.ref('/userCategories/companyRejectedUsers/{pushId}')
    .onCreate(event => {
      // Grab the current value of what was written to the Realtime Database.
      var newValue = event.data.val();
	  
	  if(newValue.mFcmToken !=null){
		   console.log('Sending Verified Notification', event.params.pushId, newValue);
	  // Create a DATA notification
    const payload = {
       data: {
        type: "3",
      }
    };
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


     return admin.messaging().sendToDevice(newValue.mFcmToken, payload,options).then((response)=> {
        console.info("Successfully sent notification")
      }).catch(function(error) {
        console.warn("Error sending notification " , error)
         });
	  }else{
		  		   console.log('Sending No Rejected Notification', event.params.pushId, newValue);
	  }
     
     
    });

