const functions = require('firebase-functions');
const admin = require('firebase-admin');
const request = require('request');
const urlencode = require('urlencode');
const rawurlencode = require('locutus/php/url/rawurlencode')
const algoliasearch = require('algoliasearch');

const ALGOLIA_APP_ID = "RHELQ0ROWI"
const ALGOLIA_API_KEY = "f16e5b97ebc784e1aebf4679a150941b"
const ALGOLIA_INDEX_NAME = 'partners'

admin.initializeApp();
var fsdb = admin.firestore();
var rtdb = admin.database();

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

      return fsdb.collection('partners').doc(newValue.mUid).get().then(doc => {
                                                                         if (!doc.exists) {
                                                                           return console.log('No such document!');
                                                                         } else {
                                                                           return admin.firestore().collection('partners').doc(newValue.mUid).update({ mDisplayName: newValue.mDisplayName, mFUID: context.params.pushId, mPhotoUrl: newValue.mImageUrl});
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

          var oldNumRatings = 0;

          if(partnerpojo.mNumRatings!==null && partnerpojo.mNumRatings!== undefined ){
             oldNumRatings = partnerpojo.mNumRatings;
          }
          console.log('oldNumRating: ', oldNumRatings);

          var newNumRatings = oldNumRatings + 1;

          // Compute new average rating
          var oldRating = 0;

          if(partnerpojo.mAvgRating!==null && partnerpojo.mAvgRating!== undefined){
             oldRating =  partnerpojo.mAvgRating;
          }
          console.log('oldRating: ', oldRating);


          var oldRatingTotal = oldRating * oldNumRatings;
          var newAvgRating = (oldRatingTotal + ratingPojo.mRitings) / newNumRatings;
          console.log('new avg rating : ', newAvgRating);


          fsdb.collection('denormalizers').doc(context.params.uid).update({ mAvgRating: newAvgRating, mNumRatings: newNumRatings });

          // Update restaurant info
          return transaction.update(partnerRef, {
            mAvgRating: newAvgRating,
            mNumRatings: newNumRatings
          });
        });
      });
    });

    exports.newRatingsNotification = functions.firestore
        .document('partners/{uid}/mRatings/{ratingId}')
        .onCreate((snapshot, context) => {

         var ratingPojo =  snapshot.data();
         const payload = {
                            data: {
                             type: "100",
                     		rating: ratingPojo.mRitings + "",
                             displayname: ratingPojo.mUserName
                           }
                         };
                     	 console.log('Sending rate notification', ratingPojo.mUserName, ratingPojo);

                         return admin.messaging().sendToDevice(ratingPojo.mReciversFCMToken, payload);


        });



         exports.updateRatingsNotification = functions.firestore
                .document('partners/{uid}/mRatings/{ratingId}')
               .onUpdate((change, context) => {

                 var ratingPojo =  change.after.data();
                 const payload = {
                                    data: {
                                     type: "101",
                             		rating: ratingPojo.mRitings + "",
                                     displayname: ratingPojo.mUserName
                                   }
                                 };

                             	 console.log('Sending rate update notification', ratingPojo.mUserName, ratingPojo);

                                 return admin.messaging().sendToDevice(ratingPojo.mReciversFCMToken, payload);


                });


    exports.ratingsUpdated = functions.firestore
        .document('partners/{uid}/mRatings/{ratingId}')
        .onUpdate((change, context) => {
          // Get pojo of the newly added rating
          var newRatingPojo =  change.after.data();
          var oldRatingPojo =  change.before.data();

          // Get a reference to the restaurant
          var partnerRef = fsdb.collection('partners').doc(context.params.uid);

          // Update aggregations in a transaction
          return fsdb.runTransaction(transaction => {
            return transaction.get(partnerRef).then(partnerDoc => {
              // Compute new number of ratings
              var partnerpojo = partnerDoc.data()

              var numRatings = partnerpojo.mNumRatings;

              // Compute new average rating
              var oldRatingTotal = partnerpojo.mAvgRating * numRatings;


              var newAvgRating = (oldRatingTotal + newRatingPojo.mRitings - oldRatingPojo.mRitings) / numRatings;
              console.log('new avg rating : ', newAvgRating);
              fsdb.collection('denormalizers').doc(context.params.uid).update({ mAvgRating: newAvgRating});

              // Update restaurant info
              return transaction.update(partnerRef, {
                mAvgRating: newAvgRating
              });
            });
          });
        });


exports.updatePresence = functions.database.ref('/chatpresence/users/{pushId}')
    .onWrite((change, context) => {

     const newValue =  change.after.val();
     const oldValue =  change.before.val();

     const uid = context.params.pushId
     console.log('chat presence updated : updating doc', context.params.pushId, newValue);

     if(newValue.active !== oldValue.active){
      fsdb.collection('partners').doc(context.params.pushId).update({ mLastActive: newValue.mTimeStamp, isActive: newValue.active });
      return fsdb.collection('denormalizers').doc(context.params.pushId).update({ mLastActive: newValue.mTimeStamp, isActive: newValue.active });
     }else{
        return fsdb.collection('partners').doc(context.params.pushId).update({ mLastActive: newValue.mTimeStamp, isActive: newValue.active });
     }




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
//
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
//
//
//
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
//
//    }

    exports.updateDenormalizedDocument = functions.firestore
        .document('denormalizers/{uid}')
        .onUpdate((change, context) => {
          // Get pojo of the newly modified doc
          var newdenormalizerPojo =  change.after.data();
          var oldDenormalizerPojo =  change.before.data();

          const uid = context.params.uid
          console.log('updateDenormalizedDocument', context.params.uid, newdenormalizerPojo);

          var newhubslist = newdenormalizerPojo.mOperationHubs;
          newhubslist['ANYWHERE'] = true;

          var oldhubslist = oldDenormalizerPojo.mOperationHubs;
          oldhubslist['ANYWHERE'] = true;





          var newlyaddedhubs = new Object();
          newlyaddedhubs = newhubslist;
          for(var hub1 in newhubslist){
          //if old hubs contain, remove it from newly added
           if(hub1 in oldhubslist){
            newlyaddedhubs[hub1]=false;
           }
          }

          var hubstoupdate = new Object();
          for(var hub3 in newhubslist){
          hubstoupdate[hub3] = true;
          }
          for(var hub2 in newhubslist){
          //if old hubs contain, remove it from newly added
           if(!(hub2 in oldhubslist)){
             delete hubstoupdate[hub2];
           }
          }

          //deleted hubs//todo
          var hubstodelete = new Object();
                    for(var hub4 in oldhubslist){
                    hubstodelete[hub4] = true;
                    }
                    for(var hub5 in oldhubslist){
                    //if old hubs contain, remove it from newly added
                     if(hub5 in newhubslist){
                       hubstodelete[hub5] = false;
                     }
                    }


          return recursiveTillAllUpdateAreDone(newdenormalizerPojo,hubstoupdate,newlyaddedhubs,hubstodelete,uid);

        });

function recursiveTillAllUpdateAreDone(newdenormalizerPojo,hubstoupdate,newlyaddedhubs,hubstodelete,uid){

    console.log('recursivee update');

    var hubx = "";
    for(var hub in hubstoupdate){

      if(hubstoupdate[hub]){

         hubx = hub;
         break;

      }

    }

    if(hubx === ""){

    //update recursion done , create newly added
    return recursiveTillAllBiCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,hubstodelete,uid);

    }

     var batch = admin.firestore().batch();

     for(hubb in hubstoupdate){

       var docRef = fsdb
       .collection('denormalised')
       .doc('routes')
       .collection(hub)
       .doc(hubb)
       .collection('companies')
       .doc(uid);

        batch.set(docRef,{mDetails: newdenormalizerPojo},{ merge: true })

     }

     return batch.commit().then(function () {

                hubstoupdate[hub] = false
                console.log('update batch commited');
                return recursiveTillAllUpdateAreDone(newdenormalizerPojo,hubstoupdate,newlyaddedhubs,hubstodelete,uid);

              });

}

function recursiveTillAllBiCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,hubstodelete,uid){

    console.log('recursive Bi create');

    var hubx = "";
    for(var hub in newlyaddedhubs){

      if(newlyaddedhubs[hub]){

         hubx = hub;
         break;

      }

    }

    if(hubx === ""){

    //recursion done
    return recursiveTillAllBiDeletesAreDone(hubstodelete,uid);

    }

     var batch = admin.firestore().batch();

     for(hubb in newlyaddedhubs){

       var docRef = fsdb
       .collection('denormalised')
       .doc('routes')
       .collection(hub)
       .doc(hubb)
       .collection('companies')
       .doc(uid);

        batch.set(docRef,{mDetails: newdenormalizerPojo, mBidValue : 0})

     }

     return batch.commit().then(function () {

     var batch2 = admin.firestore().batch();

          for(hubb2 in newlyaddedhubs){

            var docRef = fsdb
            .collection('denormalised')
            .doc('routes')
            .collection(hubb2)
            .doc(hub)
            .collection('companies')
            .doc(uid);

             batch2.set(docRef,{mDetails: newdenormalizerPojo, mBidValue : 0})

          }

          return batch2.commit().then(function () {

                newlyaddedhubs[hub] = false
                console.log('reverse batch commited');
                return recursiveTillAllBiCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,hubstodelete,uid);

              });

});
}

function recursiveTillAllBiDeletesAreDone(hubstodelete,uid){

    console.log('recursive Bi delete');

    var hubx = "";
    for(var hub in hubstodelete){

      if(hubstodelete[hub]){

         hubx = hub;
         break;

      }

    }

    if(hubx === ""){

    //recursion done
    console.log('Update Recursion Done');

    return 0;

    }

     var batch = admin.firestore().batch();

     for(hubb in hubstodelete){

       var docRef = fsdb
       .collection('denormalised')
       .doc('routes')
       .collection(hub)
       .doc(hubb)
       .collection('companies')
       .doc(uid);

        batch.delete(docRef);

     }

     return batch.commit().then(function () {

     var batch2 = admin.firestore().batch();

          for(hubb2 in hubstodelete){

            var docRef = fsdb
            .collection('denormalised')
            .doc('routes')
            .collection(hubb2)
            .doc(hub)
            .collection('companies')
            .doc(uid);

             batch2.delete(docRef);

          }

          return batch2.commit().then(function () {

                hubstodelete[hub] = false;
                console.log('reverse delete batch commited');
                return recursiveTillAllBiDeletesAreDone(hubstodelete,uid);

              });

});
}

 exports.createDenormalizeDocument = functions.firestore
        .document('denormalizers/{uid}')
        .onCreate((snap, context) => {
          // Get pojo of the newly modified doc
          var newdenormalizerPojo =  snap.data();
          const uid = context.params.uid
          console.log('createDenormalizeDocument', context.params.uid, newdenormalizerPojo);

          var newlyaddedhubs = newdenormalizerPojo.mOperationHubs;
          newlyaddedhubs['ANYWHERE'] = true;


          return recursiveTillAllCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,uid);


        });

function recursiveTillAllCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,uid){

    console.log('recursive create');

    var hubx = "";
    for(var hub in newlyaddedhubs){

      if(newlyaddedhubs[hub]){

         hubx = hub;
         break;

      }

    }

    if(hubx === ""){

    //recursion done
    return 0;

    }

     var batch = admin.firestore().batch();

     for(hubb in newlyaddedhubs){

       var docRef = fsdb
       .collection('denormalised')
       .doc('routes')
       .collection(hub)
       .doc(hubb)
       .collection('companies')
       .doc(uid);

        batch.set(docRef,{mDetails: newdenormalizerPojo, mBidValue : 0})

     }

     return batch.commit().then(function () {

                newlyaddedhubs[hub] = false
                console.log('batch commited');
                return recursiveTillAllCreatesAreDone(newdenormalizerPojo,newlyaddedhubs,uid);

              });

}

exports.addpartnerstoalgolia = functions.https.onRequest((req, res) => {

   var arr = [];
   return admin.firestore().collection("denormalizers").get().then((docs) => {


           docs.forEach((doc) => {

             let user = doc.data();
             user.objectID = doc.id;
             arr.push(user);
             console.log(doc.id,user);


           })

           var client = algoliasearch(ALGOLIA_APP_ID,ALGOLIA_API_KEY);
           var index = client.initIndex(ALGOLIA_INDEX_NAME);

           return index.saveObjects(arr,function(err,content){
            if (err) {
                   console.log(err.stack);
               }
            res.status(200).send(content);
           })


   })

});

 exports.UpdateAlgoliaPartners = functions.firestore
        .document('denormalizers/{uid}')
        .onUpdate((change, context) => {
          // Get pojo of the newly modified doc
          let newdenormalizerPojo =  change.after.data();
          newdenormalizerPojo.objectID = context.params.uid;

          console.log('UpdateAlgoliaPartners', context.params.uid, newdenormalizerPojo);

          var client = algoliasearch(ALGOLIA_APP_ID,ALGOLIA_API_KEY);
          var index = client.initIndex(ALGOLIA_INDEX_NAME);

          return index
               .saveObject(newdenormalizerPojo)
               .then(() => {
                return console.log('Firebase object indexed in Algolia', newdenormalizerPojo.objectID);
              })
              .catch(error => {
                console.error('Error when indexing contact into Algolia', error);
                return process.exit(1);
              });


        });


 exports.AddAlgoliaPartners = functions.firestore
        .document('denormalizers/{uid}')
        .onCreate((snap, context) => {
          // Get pojo of the newly modified doc
          // Get pojo of the newly modified doc
          let newdenormalizerPojo =  snap.data();
          newdenormalizerPojo.objectID = context.params.uid;

          console.log('AddAlgoliaPartners', context.params.uid, newdenormalizerPojo);

          var client = algoliasearch(ALGOLIA_APP_ID,ALGOLIA_API_KEY);
          var index = client.initIndex(ALGOLIA_INDEX_NAME);

          return index
               .saveObject(newdenormalizerPojo)
               .then(() => {
                return console.log('Firebase object indexed in Algolia', newdenormalizerPojo.objectID);
              })
              .catch(error => {
                console.error('Error when indexing contact into Algolia', error);
                return process.exit(1);
              });



        });


  exports.newProfileVisit = functions.firestore
      .document('partners/{uid}/mProfileVisits/{date}/interactors/{visitorId}')
      .onCreate((snapshot, context) => {
        // Get pojo of the newly added visit interaction document
        var interactionPojo =  snapshot.data();

        // Get a date snapshot
        var dateSnapshotRef = fsdb.collection('partners').doc(context.params.uid).collection('mProfileVisits').doc(context.params.date);
        console.log('date: ', context.params.date);

        // Update aggregations in a transaction
        return fsdb.runTransaction(transaction => {
          return transaction.get(dateSnapshotRef).then(dateSnapshotDoc => {
            // Compute new number of views this day
            var datesnapshotpojo = dateSnapshotDoc.data()
            console.log('datesnapshotpojo: ', datesnapshotpojo);

            var oldNumVisits = 0;



            if(datesnapshotpojo !== undefined){
             if(datesnapshotpojo.mNumVisits!== undefined ){
                           oldNumVisits = datesnapshotpojo.mNumVisits;
                        }
            }

            console.log('oldNumVisits: ', oldNumVisits);

            var newNumVisits = oldNumVisits + 1;

            // Update date snapshot
            return transaction.set(dateSnapshotRef, {
              mNumVisits: newNumVisits,
              mDate: context.params.date
            });
          });
        });
      });

      exports.newCallinDump = functions.firestore
            .document('partners/{uid}/mCallsDump/{date}/interactors/{visitorId}')
            .onCreate((snapshot, context) => {
              // Get pojo of the newly added visit interaction document
              var interactionPojo =  snapshot.data();

              // Get a date snapshot
              var dateSnapshotRef = fsdb.collection('partners').doc(context.params.uid).collection('mCallsDump').doc(context.params.date);
              console.log('date: ', context.params.date);

              // Update aggregations in a transaction
              return fsdb.runTransaction(transaction => {
                return transaction.get(dateSnapshotRef).then(dateSnapshotDoc => {
                  // Compute new number of views this day
                  var datesnapshotpojo = dateSnapshotDoc.data()
                  console.log('datesnapshotpojo: ', datesnapshotpojo);

                  var oldNumVisits = 0;



                  if(datesnapshotpojo !== undefined){
                   if(datesnapshotpojo.mNumVisits!== undefined ){
                                 oldNumVisits = datesnapshotpojo.mNumVisits;
                              }
                  }

                  console.log('oldNumCalls: ', oldNumVisits);

                  var newNumVisits = oldNumVisits + 1;

                  // Update date snapshot
                  return transaction.set(dateSnapshotRef, {
                    mNumDocs: newNumVisits,
                    mDate: context.params.date
                  });
                });
              });
            });


            exports.newChatinDump = functions.firestore
                          .document('partners/{uid}/mChatsDump/{date}/interactors/{visitorId}')
                          .onCreate((snapshot, context) => {
                            // Get pojo of the newly added visit interaction document
                            var interactionPojo =  snapshot.data();

                            // Get a date snapshot
                            var dateSnapshotRef = fsdb.collection('partners').doc(context.params.uid).collection('mChatsDump').doc(context.params.date);
                            console.log('date: ', context.params.date);

                            // Update aggregations in a transaction
                            return fsdb.runTransaction(transaction => {
                              return transaction.get(dateSnapshotRef).then(dateSnapshotDoc => {
                                // Compute new number of views this day
                                var datesnapshotpojo = dateSnapshotDoc.data()
                                console.log('datesnapshotpojo: ', datesnapshotpojo);

                                var oldNumVisits = 0;



                                if(datesnapshotpojo !== undefined){
                                 if(datesnapshotpojo.mNumDocs!== undefined ){
                                               oldNumVisits = datesnapshotpojo.mNumDocs;
                                            }
                                }

                                console.log('oldNumChats: ', oldNumVisits);

                                var newNumVisits = oldNumVisits + 1;

                                // Update date snapshot
                                return transaction.set(dateSnapshotRef, {
                                  mNumDocs: newNumVisits,
                                  mDate: context.params.date

                                });
                              });
                            });
            });


    exports.newContactAdded = functions.firestore
              .document('partners/{uid}/phonebook/{mobile}')
              .onCreate((snapshot, context) => {
                // Get pojo of the newly added visit interaction document
                var contactPojo =  snapshot.data();


                var reff = rtdb.ref("user_profiles");

                return reff.orderByChild("mRMN").equalTo(contactPojo.mContactNumber).once("value", function(data) {
                  // do some stuff once
                  if(data.exists()){

                  console.log('contact data exists: ',contactPojo.mContactNumber, data);



                         return fsdb
                         .collection('partners')
                         .doc(context.params.uid)
                         .collection('phonebook')
                         .doc(context.params.mobile).update({ alreadyonILN: true});



                  }else{

                   return console.log('contact dont exists: ',contactPojo.mContactNumber, data);

                  }

                });





              });

exports.newContactUpdated = functions.firestore
              .document('partners/{uid}/phonebook/{mobile}')
              .onUpdate((change, context) => {
                // Get pojo of the newly added visit interaction document
                var contactPojo =  change.after.data();

                   if(contactPojo.invited){

                     console.log('invited: ',contactPojo.mContactNumber, contactPojo);

                     //Hit SMS API
                     const hashkey = rawurlencode('hLGz8/yJ4bI-GS5XO3Pr8b3V7W2Nvoxiz3A3Kh6IWA');
                     const rmn = rawurlencode(contactPojo.mContactNumber.replace('+91',''));
                     const sender = rawurlencode('TRIPIN');
                     const appurl = rawurlencode('bit.ly/2m8n54N')
                     console.log('Sending SMS to :', contactPojo.mContactName, rmn);
                     const msg = rawurlencode('Hello. '+contactPojo.mProviderName+' have sent you this Invitation. Join Indian Logistics Network, install ILN app now. '+appurl)
                     request.get('https://api.textlocal.in/send/?apikey='+hashkey+'&numbers='+rmn+'&message='+msg+'&sender='+sender, function (error, response, body) {
                     console.log('error:', error); // Print the error if one occurred
                     console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
                     return console.log('body:', body); //Prints the response of the request.
                     });
                     return console.log("Sms api processing: ", msg)



                   }else{

                    return console.log('not invited: ',contactPojo.mContactNumber);


                   }



                });









