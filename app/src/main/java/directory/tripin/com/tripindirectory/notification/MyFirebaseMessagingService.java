package directory.tripin.com.tripindirectory.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import br.com.goncalves.pugnotification.notification.PugNotification;
import directory.tripin.com.tripindirectory.chatingactivities.ChatHeadsActivity;
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatItemPojo;
import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo;

import directory.tripin.com.tripindirectory.R;

import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.UpdateInfoPojo;
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity;
import directory.tripin.com.tripindirectory.utils.Constants;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    DocumentReference mUpdateDocRef;
    NotificationCompat.Builder generalUpdatesNotificationBuilder;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    DocumentReference mUserDocRef;


    @Override
    public void onCreate() {
        super.onCreate();
        preferenceManager = PreferenceManager.getInstance(getApplicationContext());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        mAuth = FirebaseAuth.getInstance();
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token) {

        PreferenceManager preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        preferenceManager.setFcmToken(token);

        if (mAuth.getCurrentUser() != null) {
            mUserDocRef = FirebaseFirestore.getInstance()
                    .collection("partners").document(mAuth.getUid());
            HashMap<String, String> hashMap6 = new HashMap<>();
            hashMap6.put("mFcmToken", token);
            mUserDocRef.set(hashMap6, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Refreshed token: " + "Updated to Firestore");
                }
            });

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.ARG_USERS)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(Constants.ARG_FIREBASE_TOKEN)
                    .setValue(token);
        }
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        mAuth = FirebaseAuth.getInstance();
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String type = remoteMessage.getData().get("type");
            Log.d(TAG, "type" + type);
            if (type != null) {
                if (type.equals("0")) {
                    String docId = remoteMessage.getData().get("docId");
                    Log.d(TAG, "docId" + docId);
                    sendAppUpdateNotification(docId);
                } else if (type.equals("1")) {
                    String docId = remoteMessage.getData().get("docId");
                    Log.d(TAG, "docId" + docId);
                    sendNewsUpdateNotification(docId);
                } else if (type.equals("5")) {
                    String body = Objects.requireNonNull(remoteMessage.getNotification()).getBody();
                    String title = remoteMessage.getNotification().getTitle();
                    String postId = remoteMessage.getData().get("postId");

                    sendLoadboardNotification(title, body, postId);
                } else if (type.equals("6")) {
                    //OneToOneChat New Message Notification
                    String docId = remoteMessage.getData().get("docId");
                    String chatroomId = remoteMessage.getData().get("chatroomId");
                    Log.d(TAG, "new one to one msg");

                    if (chatroomId != null && docId != null) {
                        sendNewChatMsgNotification(chatroomId, docId);

                    } else {
                        Log.d(TAG, "ids null");
                    }
                }else if (type.equals("100")) {
                    //OneToOneChat New Message Notification
                    String rating = remoteMessage.getData().get("rating");
                    String dispmayname = remoteMessage.getData().get("displayname");
                    Log.d(TAG, "new rating");

                    if (rating != null && dispmayname != null) {
                        sendNewRatingNotification(rating, dispmayname);

                    } else {
                        Log.d(TAG, "ids null");
                    }
                }else if (type.equals("101")) {
                    //OneToOneChat New Message Notification
                    String rating = remoteMessage.getData().get("rating");
                    String dispmayname = remoteMessage.getData().get("displayname");
                    Log.d(TAG, "edited rating");

                    if (rating != null && dispmayname != null) {
                        sendEditRatingNotification(rating, dispmayname);
                    } else {
                        Log.d(TAG, "ids null");
                    }
                }
            }


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }






    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }







    private void sendNewChatMsgNotification(final String chatroomId, String docId) {


        FirebaseFirestore.getInstance().collection("chats").document("chatrooms").collection(chatroomId).document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    ChatItemPojo chatItemPojo = documentSnapshot.toObject(ChatItemPojo.class);

                    FirebaseDatabase.getInstance().getReference().child("chatpresence").child("users").child(chatItemPojo.getmReciversUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()) {
                                UserPresensePojo userPresensePojo = dataSnapshot.getValue(UserPresensePojo.class);
                                if (userPresensePojo != null) {
                                    if (userPresensePojo.getmChatroomId() != null) {
                                        if (userPresensePojo.getmChatroomId().equals(chatroomId) && userPresensePojo.getActive()) {
                                            return;
                                        }
                                    }
                                }
                            }

                            Intent intent = new Intent(getApplicationContext(), ChatHeadsActivity.class);

                            String title = "";
                            if (chatItemPojo.getmDisplayName().isEmpty()) {
                                title = chatItemPojo.getmRMN() + " :";
                            } else {
                                title = chatItemPojo.getmDisplayName() + " :";
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT);

                            String channelId = "ILN notification";
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle(title)
                                            .setContentText(chatItemPojo.getmChatMesssage())
                                            .setAutoCancel(true)
                                            .setGroup("ILN Chats")
                                            .setSound(defaultSoundUri)
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                            notificationManager.notify(6/* ID of notification */, notificationBuilder.build());
                            Log.v(TAG, "new one to one msg notified");

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Log.d(TAG, "new one to one msg doc dont exist1");
                }


            }
        });
    }



    private void sendLoadboardNotification(String messageBody, String messageTitle, String postId) {

        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.d(TAG, "new loadboard doc");

        PugNotification.with(getApplicationContext())
                .load()
                .identifier(5)
                .title(messageTitle)
                .message(messageBody)
                .smallIcon(R.drawable.ic_notification)
                .largeIcon(R.mipmap.ic_launcher_round)
                .flags(Notification.DEFAULT_ALL)
                .click(directory.tripin.com.tripindirectory.newlookcode.activities.FSLoadBoardActivity.class)
                .color(R.color.primaryColor)
                .lights(Color.RED, 1, 1)
                .sound(defaultRingtoneUri)
                .autoCancel(true)
                .simple()
                .build();
    }





    private void sendNewsUpdateNotification(String docId) {

//For Production
        mUpdateDocRef = FirebaseFirestore.getInstance()
                .collection("updates").document(docId);

//        mUpdateDocRef = FirebaseFirestore.getInstance()
//                .collection("updatestest").document(docId);


        mUpdateDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UpdateInfoPojo updateInfoPojo = documentSnapshot.toObject(UpdateInfoPojo.class);
                Intent intentToURL;
                String url = updateInfoPojo.getmUrl();
                intentToURL = new Intent(Intent.ACTION_VIEW);
                intentToURL.setData(Uri.parse(url));

                intentToURL.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intentToURL,
                        PendingIntent.FLAG_ONE_SHOT);

                String channelId = "ILN notification";
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                generalUpdatesNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(updateInfoPojo.getmTitle())
                        .setContentText(updateInfoPojo.getmDiscription())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

                if (!updateInfoPojo.getmImageUrl().isEmpty()) {
                    Log.d(TAG, "have an mage url: " + updateInfoPojo.getmImageUrl());

                    new generatePictureStyleNotification(updateInfoPojo.getmImageUrl()).execute();


                } else {

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0 /* ID of notification */, generalUpdatesNotificationBuilder.build());
                }

            }
        });

    }

    private void sendAppUpdateNotification(String docId) {
//for production
        mUpdateDocRef = FirebaseFirestore.getInstance()
                .collection("updates").document(docId);

        //for testing
//        mUpdateDocRef = FirebaseFirestore.getInstance()
//                .collection("updatestest").document(docId);


        mUpdateDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UpdateInfoPojo updateInfoPojo = documentSnapshot.toObject(UpdateInfoPojo.class);
                final String appPackageName = getPackageName(); // package name of the app
                Intent intentToPlayStore;
                try {
                    intentToPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                } catch (android.content.ActivityNotFoundException anfe) {
                    intentToPlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
                }
                intentToPlayStore.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intentToPlayStore,
                        PendingIntent.FLAG_ONE_SHOT);
                String channelId = "ILN notification";
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                generalUpdatesNotificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(updateInfoPojo.getmTitle())
                        .setContentText(updateInfoPojo.getmDiscription())
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);


                if (!updateInfoPojo.getmImageUrl().isEmpty()) {
                    Log.d(TAG, "have an mage url: " + updateInfoPojo.getmImageUrl());

                    new generatePictureStyleNotification(updateInfoPojo.getmImageUrl()).execute();

                } else {

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    notificationManager.notify(0 /* ID of notification */, generalUpdatesNotificationBuilder.build());
                }
            }
        });
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
//        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
//        Job myJob = dispatcher.newJobBuilder()
//                .setService(MyJobService.class)
//                .setTag("my-job-tag")
//                .build();
//        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }



    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(String imageUrl) {
            super();
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute");
            if (result != null) {
                generalUpdatesNotificationBuilder.setLargeIcon(result)
                        .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(result));
            }
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0 /* ID of notification */, generalUpdatesNotificationBuilder.build());
        }
    }



    private void sendNewRatingNotification(final String rating, final String displayname) {

        Intent intent = new Intent(getApplicationContext(), CompanyProfileDisplayActivity.class);
        intent.putExtra("uid",preferenceManager.getUserId());
        intent.putExtra("rmn",preferenceManager.getRMN());
        intent.putExtra("fuid",preferenceManager.getFuid());
        String title = "New Rating: "+rating;
        String subtitle = "by "+displayname;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(subtitle)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(100 /* ID of notification */, notificationBuilder.build());

    }

    private void sendEditRatingNotification(final String rating, final String displayname) {

        Intent intent = new Intent(getApplicationContext(), CompanyProfileDisplayActivity.class);
        intent.putExtra("uid",preferenceManager.getUserId());
        intent.putExtra("rmn",preferenceManager.getRMN());
        intent.putExtra("fuid",preferenceManager.getFuid());
        String title = "Rating Edited: "+rating;
        String subtitle = "by "+displayname;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(subtitle)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(101 /* ID of notification */, notificationBuilder.build());

    }
}
