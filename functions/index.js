//import firebase functions modules
const functions = require('firebase-functions');
//import admin module
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');
admin.initializeApp(functions.config().firebase);

//const gmailEmail = functions.config().gmail.email;
//const gmailPassword = functions.config().gmail.password;

const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: "ravishankar.ahirwar@tripin.co.in",
    pass: "rrrrrr25121983",
  },
});
const APP_NAME = 'Indian Logistics Network';


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
	
	
	//one to one message notification
exports.onetoonechat = functions.firestore
  .document('chats/chatrooms/{chatroomId}/{messageId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "6",
		chatroomId: event.params.chatroomId,
        docId: event.params.messageId
      }
    };
	
	 console.log('Sending chat notification', event.params.chatroomId, newValue);
   
    return admin.messaging().sendToDevice(newValue.mReciversFcmToken, payload);
});

	
	//Comment on load post
exports.commentonloadnotif = functions.firestore
  .document('loads/{loadId}/mCommentsCollection/{commentId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "7",
		loadId: event.params.loadId,
        docId: event.params.commentId
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic(event.params.loadId, payload, options);
});

//Comment on fleet post
exports.commentonfleetnotif = functions.firestore
  .document('fleets/{fleetId}/mCommentsCollection/{commentId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "8",
		fleetId: event.params.fleetId,
        docId: event.params.commentId
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic(event.params.fleetId, payload, options);
});

//Quote on load post
exports.quoteonloadnotif = functions.firestore
  .document('loads/{loadId}/mQuotesCollection/{quoteId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "9",
		loadId: event.params.loadId,
        docId: event.params.quoteId
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToDevice(newValue.mFcmToken, payload)
});

//Quote on fleet post
exports.quoteonfleetnotif = functions.firestore
  .document('fleets/{fleetId}/mQuotesCollection/{quoteId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "10",
		fleetId: event.params.fleetId,
        docId: event.params.quoteId
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToDevice(newValue.mFcmToken, payload)
});

	//New load post
exports.commentonloadnotif = functions.firestore
  .document('loads/{loadId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "11",
		loadId: event.params.loadId,
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("NewLoadPost", payload, options);
});

//New load post
exports.newloadnotif = functions.firestore
  .document('loads/{loadId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "11",
		loadId: event.params.loadId,
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("NewLoadPost", payload, options);
});

//New load post
exports.newfleetnotif = functions.firestore
  .document('fleets/{fleetId}')
  .onCreate(event => {
    var newValue = event.data.data();

	// Create a DATA notification
    const payload = {
       data: {
        type: "12",
		loadId: event.params.fleetId,
      }
    };
	
	 const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };


    return admin.messaging().sendToTopic("NewFleetPost", payload, options);
});

exports.loadboardNotification = functions.database.ref('/posts/{pushId}')
        .onCreate(event => {
          // Grab the current value of what was written to the Realtime Database.
          var newValue = event.data.val();
          console.log('LoadBoardPost', event.params.pushId);
               // Create a notification
                   const payload = {
                    data: {
                               type: "5",
                               postId: event.params.pushId
                          },
                       notification: {
                           title: "Load Posted by " + newValue.mAuthor,
                           body: newValue.mSource + " To " + newValue.mDestination,
                           sound: "default"
                       }
                   };
        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        };
         return admin.messaging().sendToTopic("loadboardNotification", payload, options);
        });


    	exports.loadboardNotification = functions.database.ref('/posts/{pushId}')
        .onCreate(event => {
          // Grab the current value of what was written to the Realtime Database.
          var newValue = event.data.val();
          console.log('LoadBoardPost', event.params.pushId);

    		   // Create a notification
                   const payload = {

                    data: {
                               type: "5",
                               postId: event.params.pushId
                          },

                       notification: {
                           title: "Load Posted by " + newValue.mAuthor,
                           body: newValue.mSource + " To " + newValue.mDestination,
                           sound: "default"
                       }
                   };

        const options = {
            priority: "high",
            timeToLive: 60 * 60 * 24
        };

         return admin.messaging().sendToTopic("loadboardNotification", payload, options);
        });
exports.sendWelcomeEmail = functions.database.ref('/posts/{pushId}').onCreate(event => {
// [END onCreateTrigger]
  // [START eventAttributes]
//  const email = user.email; // The email of the user.
//  const displayName = user.displayName; // The display name of the user.
  // [END eventAttributes]

  return sendWelcomeEmail("ravishankar.ahirwar@gmail.com,ravishankar.malaysia@gmail.com", "Ravi");
});

// Sends a welcome email to the given user.
function sendWelcomeEmail(email, displayName) {
  const mailOptions = {
    from: `${APP_NAME} <noreply@firebase.com>`,
    bcc: email,
  };
  // The user subscribed to the newsletter.
  mailOptions.subject = `Welcome to ${APP_NAME}!`;
  mailOptions.text = `Hey ${displayName || ''}! Welcome to ${APP_NAME}. I hope you will enjoy our service.`;
  return mailTransport.sendMail(mailOptions).then(() => {
    return console.log('New welcome email sent to:', email);
  });
}
