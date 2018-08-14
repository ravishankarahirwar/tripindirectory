const functions = require('firebase-functions');
const admin = require('firebase-admin');
const request = require('request');
const urlencode = require('urlencode');
const rawurlencode = require('locutus/php/url/rawurlencode')

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


      if(newValue.mReciversFcmToken===null){
      //Hit SMS API
      console.log('Sending SMS :', context.params.chatroomId, newValue);
      const hashkey = rawurlencode('hLGz8/yJ4bI-GS5XO3Pr8b3V7W2Nvoxiz3A3Kh6IWA');
      const rmn = rawurlencode(newValue.mORMN.substring(1));
      const sender = rawurlencode('TRIPIN');
      const appurl = rawurlencode('bit.ly/2m8n54N')
      console.log('Sending SMS to :', context.params.chatroomId, rmn);
      const msg = rawurlencode('Hello ILN User. '+newValue.mDisplayName+' have sent you a new msg in chat. Do not miss the opportunity. Install the updated ILN app now. '+appurl)
      request.get('https://api.textlocal.in/send/?apikey='+hashkey+'&numbers='+rmn+'&message='+msg+'&sender='+sender, function (error, response, body) {
      console.log('error:', error); // Print the error if one occurred
      console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
      return console.log('body:', body); //Prints the response of the request.
       });
        return console.log("Sms api processing: ", msg)
      }else{

      if(newValue.mOFUID===null){

       //Hit SMS API
            console.log('Sending SMS :', context.params.chatroomId, newValue);
            const hashkey = rawurlencode('hLGz8/yJ4bI-GS5XO3Pr8b3V7W2Nvoxiz3A3Kh6IWA');
            const rmn = rawurlencode(newValue.mORMN.substring(1));
            const sender = rawurlencode('TRIPIN');
            const appurl = rawurlencode('bit.ly/2m8n54N')
            console.log('Sending SMS to :', context.params.chatroomId, rmn);
            const msg = rawurlencode('Hello ILN User. '+newValue.mDisplayName+' have sent you a new msg in chat. Do not miss the opportunity. Install the updated ILN app now. '+appurl)
            request.get('https://api.textlocal.in/send/?apikey='+hashkey+'&numbers='+rmn+'&message='+msg+'&sender='+sender, function (error, response, body) {
            console.log('error:', error); // Print the error if one occurred
            console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
            return console.log('body:', body); //Prints the response of the request.
             });
              return console.log("Sms api processing: ", msg)

      }else{
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
      }

      }



      });

//Make the initial mAccountStatusValue 0
exports.facebookprofilecreated =  functions.database.ref('/user_profiles/{pushId}')
  .onCreate((snapshot, context) => {
      const newValue = snapshot.val();
      console.log('new facebook user created', context.params.pushId, newValue);
      admin.firestore().collection('partners').doc(newValue.mUid).update({ mFUID: context.params.pushId });
      admin.firestore().collection('partners').doc(newValue.mUid).update({ mDisplayName: newValue.mDisplayName });
      return admin.firestore().collection('partners').doc(newValue.mUid).update({ mPhotoUrl: newValue.mImageUrl });

});



