const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.newLoadpost = functions.database.ref('/posts/{pushId}')
    .onCreate((snapshot, context) => {
      // Grab the current value of what was written to the Realtime Database.
      const original = snapshot.val();
      console.log('Uppercasing', context.params.pushId, original);
       const payload = {
                    data: {
                               type: "5",
                               postId: context.params.pushId
                          },
                       notification: {
                           title: "Load Posted by " + original.mAuthor,
                           body: original.mSource + " To " + original.mDestination,
                           sound: "default"
                       }
                   };
        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        };
         return admin.messaging().sendToTopic("loadboardNotification", payload, options);
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
	  if(newValue.mFcmToken !==null){
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
        return console.info("Successfully sent notification")
      }).catch(function(error) {

        return console.warn("Error sending notification " , error)
         });
	  }else{
	        throw new Error("Token Null")
	  }

    });

	exports.newRejectedCompany = functions.database.ref('/userCategories/companyRejectedUsers/{pushId}')
    .onCreate(event => {
      // Grab the current value of what was written to the Realtime Database.
      var newValue = event.data.val();
	  if(newValue.mFcmToken !==null){
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
        return console.info("Successfully sent notification")
      }).catch(function(error) {
        return console.warn("Error sending notification " , error)
         });
	  }
    });


	//one to one message notification
exports.onetoonechat = functions.firestore
  .document('chats/chatrooms/{chatroomId}/{messageId}')
  .onCreate((snap, context) => {

      const newValue = snap.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "6",
		chatroomId: context.params.chatroomId,
        docId: context.params.messageId
      }
    };

	 console.log('Sending chat notification', context.params.chatroomId, newValue);

    return admin.messaging().sendToDevice(newValue.mReciversFcmToken, payload);

      });



