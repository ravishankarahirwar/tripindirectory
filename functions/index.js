const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.makeUppercase = functions.database.ref('/posts/{pushId}')
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
