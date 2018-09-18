const functions = require('firebase-functions');
const admin = require('firebase-admin');
const request = require('request');
const urlencode = require('urlencode');
const rawurlencode = require('locutus/php/url/rawurlencode')

admin.initializeApp();
var fsdb = admin.firestore();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//general updates PRODUCTION function
exports.createUpdate = functions.firestore
  .document('updates/{documentId}')
  .onCreate((snap, context) => {
    var newValue = snap.data();

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

////general updates Test function
//exports.createUpdateTest = functions.firestore
//  .document('updatestest/{documentId}')
//  .onCreate((snap, context) => {
//    var newValue = snap.data();
//
//	// Create a DATA notification
//    const payload = {
//       data: {
//        type: newValue.mType,
//        docId: newValue.mTimeStamp
//      }
//    };
//    const options = {
//        priority: "high",
//        timeToLive: 60 * 60 * 24
//    };
//    return admin.messaging().sendToTopic("generalUpdatesTest", payload, options);
//});

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


//exports.newVerifiedCompany = functions.database.ref('/userCategories/companyVerifiedUsers/{pushId}')
//    .onCreate(event => {
//      // Grab the current value of what was written to the Realtime Database.
//      var newValue = event.data.val();
//	  if(newValue.mFcmToken !==null){
//		   console.log('Sending Verified Notification', event.params.pushId, newValue);
//	  // Create a DATA notification
//    const payload = {
//       data: {
//        type: "2",
//        mCompanyName: newValue.mCompanyName,
//      }
//    };
//    const options = {
//        priority: "high",
//        timeToLive: 60 * 60 * 24
//    };
//
//
//     return admin.messaging().sendToDevice(newValue.mFcmToken, payload,options).then((response)=> {
//        return console.info("Successfully sent notification")
//      }).catch(function(error) {
//
//        return console.warn("Error sending notification " , error)
//         });
//	  }else{
//	        throw new Error("Token Null")
//	  }
//
//    });

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

//updating values in partners document
exports.facebookprofilecreated =  functions.database.ref('/user_profiles/{pushId}')
  .onCreate((snapshot, context) => {
      const newValue = snapshot.val();
      console.log('new facebook user created', context.params.pushId, newValue);

      admin.firestore().collection('partners').doc(newValue.mUid).get().then(doc => {
                                                                         if (!doc.exists) {
                                                                           return console.log('No such document!');
                                                                         } else {
                                                                           admin.firestore().collection('partners').doc(newValue.mUid).update({ mFUID: context.params.pushId });
                                                                           admin.firestore().collection('partners').doc(newValue.mUid).update({ mDisplayName: newValue.mDisplayName });
                                                                           return admin.firestore().collection('partners').doc(newValue.mUid).update({ mPhotoUrl: newValue.mImageUrl });
                                                                         }
                                                                       })
                                                                       .catch(err => {
                                                                         return console.log('Error getting document', err);
                                                                       });


});

exports.newRatings = functions.firestore
    .document('partners/{uid}/mRatings/{ratingId}')
    .onCreate((snapshot, context) => {
      // Get pojo of the newly added rating
      var ratingPojo =  snapshot.data();

      // Get a reference to the restaurant
      var partnerRef = fsdb.collection('partners').doc(context.params.uid);

      // Update aggregations in a transaction
      return fsdb.runTransaction(transaction => {
        return transaction.get(partnerRef).then(partnerDoc => {
          // Compute new number of ratings
          var partnerpojo = partnerDoc.data()
          var newNumRatings = partnerpojo.mNumRatings + 1;

          // Compute new average rating
          var oldRatingTotal = partnerpojo.mAvgRating * partnerpojo.mNumRatings;
          var newAvgRating = (oldRatingTotal + ratingPojo.mRitings) / newNumRatings;

          // Update restaurant info
          return transaction.update(partnerRef, {
            mAvgRating: newAvgRating,
            mNumRatings: newNumRatings
          });
        });
      });
    });

//    exports.ratingsUpdated = functions.firestore
//        .document('partners/{uid}/mRatings/{ratingId}')
//        .onUpdate((change, context) => {
//          // Get pojo of the newly added rating
//          var newRatingPojo =  change.after.data();
//          var oldRatingPojo =  change.before.data();
//
//          // Get a reference to the restaurant
//          var partnerRef = fsdb.collection('partners').doc(context.params.uid);
//
//          // Update aggregations in a transaction
//          return fsdb.runTransaction(transaction => {
//            return transaction.get(partnerRef).then(partnerDoc => {
//              // Compute new number of ratings
//              var partnerpojo = partnerDoc.data()
//
//              var numRatings = partnerpojo.mNumRatings;
//
//              // Compute new average rating
//              var oldRatingTotal = partnerpojo.mAvgRating * numRatings;
//              var oldR = oldRatingPojo.mRitings * numRatings;
//              var newR = newRatingPojo.mRitings * numRatings;
//
//
//
//              var newAvgRating = (oldRatingTotal + newRatingPojo.mRitings - oldRatingPojo.mRitings) / numRatings;
//
//              // Update restaurant info
//              return transaction.update(partnerRef, {
//                mAvgRating: newAvgRating
//              });
//            });
//          });
//        });


exports.updatePresence = functions.database.ref('/chatpresence/users/{pushId}')
    .onWrite((change, context) => {

     const newValue =  change.after.val();
     const uid = context.params.pushId
     console.log('chat presence updated : updating doc', context.params.pushId, newValue);

     admin.firestore().collection('partners').doc(context.params.pushId).update({ mLastActive: newValue.mTimeStamp });
     return admin.firestore().collection('partners').doc(context.params.pushId).update({ isActive: newValue.active });

    });

//exports.denormalizePresence = functions.database.ref('/chatpresence/users/{pushId}')
//    .onWrite((change, context) => {
//
//     const newValue =  change.after.val();
//     const uid = context.params.pushId
//     console.log('chat presence updated', context.params.pushId, newValue);
//
//    //read the list of hubs of this user
//     admin.firestore().collection('hubslookup').doc(context.params.pushId).get().then(doc => {
//                                                                             if (!doc.exists) {
//                                                                               return console.log('No such document!');
//                                                                             } else {
//                                                                               console.log('Document data:', doc.data());
//                                                                               const docVal = doc.data();
//
//                                                                               var routes = [];
//                                                                               routes.push("ALL");
//                                                                               for(hub in docVal.mHubs){
//                                                                               for(hubss in docVal.mHubs){
//                                                                               routes.push(docVal.mHubs[hub]+"_"+docVal.mHubs[hubss]);
//                                                                               }
//                                                                               }
////                                                                               console.log('Generated List:', routes);
//
//                                                                              return itirateThroughBatch(routes,newValue, uid);
//
//
//                                                                             }
//                                                                           })
//                                                                           .catch(err => {
//                                                                             return console.log('Error getting document', err);
//                                                                           });
//
//
//
//    });

//    function itirateThroughBatch(routes,newValue,uid){
//
//         var batch = admin.firestore().batch();
//
//         for(route in routes){
//              var docRef = admin.firestore().collection('denormalised').doc('routes').collection(routes[route]).doc(uid);
//              batch.update(docRef, { mLastActive: newValue.mTimeStamp });
//              batch.update(docRef, { isActive: newValue.active });
//              console.log('route: '+route+" : "+routes[route]);
//         }
//         // Commit the batch
//          return batch.commit().then(function () {
//            throw console.log('Batching Done');
//         });



//    console.log('itirateThroughBatch started '+routes.length+' '+newValue.data+' '+uid);
//
//     var batch = admin.firestore().batch();
//     var count = 450;
//     var trimmedroutes = routes;
//
//     for(route in routes){
//          var docRef = admin.firestore().collection('denormalised').doc('routes').collection(routes[route]).doc(uid);
//          batch.update(docRef, { mLastActive: newValue.mTimeStamp });
//          batch.update(docRef, { isActive: newValue.active });
//          console.log('route: '+route+" : "+routes[route]);
//
//          count = count - 2;
//          trimmedroutes = routes.splice(route,1);
//          if(count<0){
//          console.log('Break')
//           break;
//          }
//     }
//     // Commit the batch
//       batch.commit().then(function () {
//       if(trimmedroutes.length === 0){
//        throw console.log('Batching Done');
//       }else{
//       itirateThroughBatch(trimmedroutes,newValue,uid);
//       throw console.log('Initation Started Again');
//
//       }
//     });

//    }





