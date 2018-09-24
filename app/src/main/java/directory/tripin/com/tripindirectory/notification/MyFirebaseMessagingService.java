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

import directory.tripin.com.tripindirectory.chatingactivities.ChatHeadsActivity;
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity;
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatItemPojo;
import directory.tripin.com.tripindirectory.chatingactivities.models.UserPresensePojo;
import directory.tripin.com.tripindirectory.Messaging.Activity.ChatActivity;
import directory.tripin.com.tripindirectory.formactivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.loadboardactivities.FleetDetailsActivity;
import directory.tripin.com.tripindirectory.loadboardactivities.LoadBoardActivity;
import directory.tripin.com.tripindirectory.loadboardactivities.LoadDetailsActivity;
import directory.tripin.com.tripindirectory.loadboardactivities.models.CommentPojo;
import directory.tripin.com.tripindirectory.loadboardactivities.models.FleetPostPojo;
import directory.tripin.com.tripindirectory.loadboardactivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.loadboardactivities.models.QuotePojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.MainActivity;
import directory.tripin.com.tripindirectory.forum.PostDetailActivity;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.UpdateInfoPojo;
import directory.tripin.com.tripindirectory.utils.Constants;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    DocumentReference mUpdateDocRef;
    NotificationCompat.Builder generalUpdatesNotificationBuilder;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    DocumentReference mUserDocRef;


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
                } else if (type.equals("2")) {
                    //verification request approved notification
                    String mCompName = remoteMessage.getData().get("mCompanyName");
                    sendVerificationApprovedNotification(mCompName);

                } else if (type.equals("3")) {
                    //verification request rejected notification
                    sendVerificationRejectedNotification();

                } else if (type.equals("5")) {
                    String body = remoteMessage.getNotification().getBody();
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
                } else {


                    if (type.equals("7")) {
                        //New comment on loadpost
                        String docId = remoteMessage.getData().get("docId");
                        String loadId = remoteMessage.getData().get("loadId");
                        if (loadId != null && docId != null) {
                            sendNewLoadCommentNotification(loadId, docId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }

                    if (type.equals("8")) {
                        //New comment on fleetpost
                        String docId = remoteMessage.getData().get("docId");
                        String loadId = remoteMessage.getData().get("fleetId");
                        if (loadId != null && docId != null) {
                            sendNewFleetCommentNotification(loadId, docId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }

                    if (type.equals("9")) {
                        //New quote on loadpost
                        String docId = remoteMessage.getData().get("docId");
                        String loadId = remoteMessage.getData().get("loadId");
                        if (loadId != null && docId != null) {
                            sendNewLoadQuoteNotification(loadId, docId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }

                    if (type.equals("10")) {
                        //New quote on fleetpost
                        String docId = remoteMessage.getData().get("docId");
                        String loadId = remoteMessage.getData().get("fleetId");
                        if (loadId != null && docId != null) {
                            sendNewFleetQuoteNotification(loadId, docId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }

                    if (type.equals("11")) {
                        //New loadpost
                        String loadId = remoteMessage.getData().get("loadId");
                        if (loadId != null) {
                            sendNewLoadPostNotification(loadId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }
                    if (type.equals("12")) {
                        //New fleetpost
                        String fleetId = remoteMessage.getData().get("fleetId");
                        if (fleetId != null) {
                            sendNewFleetPostNotification(fleetId);

                        } else {
                            Log.d(TAG, "ids null");
                        }
                    }
                }
            } else {
                String messageBody = Objects.requireNonNull(remoteMessage.getNotification()).getBody();
                String messageTitle = remoteMessage.getNotification().getTitle();
                Log.d(TAG, "Message Notification Body: " + messageBody);
                sendNotification(messageBody, messageTitle);
            }


            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        } else if (remoteMessage.getNotification() != null) {
            String messageBody = remoteMessage.getNotification().getBody();
            String messageTitle = remoteMessage.getNotification().getTitle();
            Log.d(TAG, "Message Notification Body: " + messageBody);
            sendNotification(messageBody, messageTitle);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNewFleetPostNotification(final String fleetId) {
        FirebaseFirestore.getInstance().collection("fleets").document(fleetId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    FleetPostPojo fleetPostPojo = documentSnapshot.toObject(FleetPostPojo.class);

                    if (!fleetPostPojo.getmPostersUid().equals(FirebaseAuth.getInstance().getUid())) {
                        Intent intent = new Intent(getApplicationContext(), FleetDetailsActivity.class);
                        intent.putExtra("docId", fleetId);
                        String title = "New Fleet Posted";
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        String channelId = "ILN notification";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), channelId)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setContentTitle(title)
                                        .setContentText(fleetPostPojo.getmSourceCity() + " to " + fleetPostPojo.getmDestinationCity())
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FleetDetailsActivity.class);
                        intent.putExtra("docId", fleetId);
                        String title = "Hi ILN User";
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        String channelId = "ILN notification";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), channelId)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setContentTitle(title)
                                        .setContentText("Your Fleet is successfully posted on LoadBoard!")
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }

                }
            }
        });
    }

    private void sendNewLoadPostNotification(final String loadId) {
        FirebaseFirestore.getInstance().collection("loads").document(loadId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    LoadPostPojo loadPostPojo = documentSnapshot.toObject(LoadPostPojo.class);

                    if (!loadPostPojo.getmPostersUid().equals(FirebaseAuth.getInstance().getUid())) {
                        Intent intent = new Intent(getApplicationContext(), LoadDetailsActivity.class);
                        intent.putExtra("docId", loadId);
                        String title = "New Load Posted";
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        String channelId = "ILN notification";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), channelId)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setContentTitle(title)
                                        .setContentText(loadPostPojo.getmSourceCity() + " to " + loadPostPojo.getmDestinationCity())
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoadDetailsActivity.class);
                        intent.putExtra("docId", loadId);
                        String title = "Hi ILN User";
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                PendingIntent.FLAG_ONE_SHOT);

                        String channelId = "ILN notification";
                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), channelId)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setContentTitle(title)
                                        .setContentText("Your Load is posted successfully on Loadboard")
                                        .setAutoCancel(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                    }

                }
            }
        });

    }

    private void sendNewFleetQuoteNotification(String loadId, String docId) {
        FirebaseFirestore.getInstance().collection("fleets")
                .document(loadId)
                .collection("mQuotesCollection")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            QuotePojo quotePojo = documentSnapshot.toObject(QuotePojo.class);

                            Intent intent = new Intent(getApplicationContext(), LoadBoardActivity.class);
                            intent.putExtra("frag", "3");
                            String title = "New Quote on your Fleetpost";
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT);

                            String channelId = "ILN notification";
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setContentTitle(title)
                                            .setContentText(quotePojo.getmQuoteAmount() + "₹ by : " + quotePojo.getmRMN())
                                            .setAutoCancel(true)
                                            .setSound(defaultSoundUri)
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }


                    }
                });
    }

    private void sendNewLoadQuoteNotification(final String loadId, String docId) {
        FirebaseFirestore.getInstance().collection("loads")
                .document(loadId)
                .collection("mQuotesCollection")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            QuotePojo quotePojo = documentSnapshot.toObject(QuotePojo.class);

                            Intent intent = new Intent(getApplicationContext(), LoadBoardActivity.class);
                            intent.putExtra("frag", "3");
                            String title = "New Quote on your Loadpost";
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                    PendingIntent.FLAG_ONE_SHOT);

                            String channelId = "ILN notification";
                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            NotificationCompat.Builder notificationBuilder =
                                    new NotificationCompat.Builder(getApplicationContext(), channelId)
                                            .setSmallIcon(R.drawable.ic_notification)
                                            .setContentTitle(title)
                                            .setContentText(quotePojo.getmQuoteAmount() + "₹ by : " + quotePojo.getmRMN())
                                            .setAutoCancel(true)
                                            .setSound(defaultSoundUri)
                                            .setContentIntent(pendingIntent);

                            NotificationManager notificationManager =
                                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                        }


                    }
                });
    }

    private void sendNewFleetCommentNotification(final String loadId, final String docId) {
        FirebaseFirestore.getInstance().collection("fleets")
                .document(loadId)
                .collection("mCommentsCollection")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            CommentPojo commentPojo = documentSnapshot.toObject(CommentPojo.class);

                            if (!commentPojo.getmUid().equals(FirebaseAuth.getInstance().getUid())) {
                                Intent intent = new Intent(getApplicationContext(), FleetDetailsActivity.class);
                                intent.putExtra("docId", loadId);
                                String title = "New Comment on fleetpost :";
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                        PendingIntent.FLAG_ONE_SHOT);

                                String channelId = "ILN notification";
                                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(getApplicationContext(), channelId)
                                                .setSmallIcon(R.drawable.ic_notification)
                                                .setContentTitle(title)
                                                .setContentText(commentPojo.getmCommentText())
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(pendingIntent);

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                            }

                        }
                    }
                });
    }

    private void sendNewLoadCommentNotification(final String loadId, final String docId) {
        FirebaseFirestore.getInstance().collection("loads")
                .document(loadId)
                .collection("mCommentsCollection")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            CommentPojo commentPojo = documentSnapshot.toObject(CommentPojo.class);

                            if (!commentPojo.getmUid().equals(FirebaseAuth.getInstance().getUid())) {
                                Intent intent = new Intent(getApplicationContext(), LoadDetailsActivity.class);
                                intent.putExtra("docId", loadId);
                                String title = "New Comment on loadpost:";
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0 /* Request code */, intent,
                                        PendingIntent.FLAG_ONE_SHOT);

                                String channelId = "ILN notification";
                                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(getApplicationContext(), channelId)
                                                .setSmallIcon(R.drawable.ic_notification)
                                                .setContentTitle(title)
                                                .setContentText(commentPojo.getmCommentText())
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(pendingIntent);

                                NotificationManager notificationManager =
                                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                            }


                        }
                    }
                });
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

//    private void sendVerificationRejectedNotification() {
//        Intent intent = new Intent(this, CompanyInfoActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//    }

    private void sendLoadboardNotification(String messageBody, String messageTitle, String postId) {
        Intent intent;
        //if user signed in
        preferenceManager = PreferenceManager.getInstance(getApplicationContext());
        if (preferenceManager.isOnNewLook()) {
            intent = new Intent(this, directory.tripin.com.tripindirectory.newlookcode.activities.LoadBoardActivity.class);
        } else {
            if (postId != null) {
                intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postId);
            } else {
                intent = new Intent(this, directory.tripin.com.tripindirectory.forum.MainActivity.class);
            }
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification ";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendVerificationRejectedNotification() {
        Intent intent = new Intent(this, CompanyInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("ILN Verification Rejected!")
                        .setContentText("Dear User! your company verification request is rejected. Please be serious next time")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendVerificationApprovedNotification(String mCompName) {
        Intent intent = new Intent(this, CompanyInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("ILN Company Verification")
                        .setContentText("Congratulations " + mCompName + "! your company verification request s approved.")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

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

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        int random = (int)System.currentTimeMillis();
        int random = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, random /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "ILN notification ";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(messageTitle)
                        .setLights(Color.RED, 500, 500)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();
        // Clear the notification when it is pressed
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(random, notification);
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

    private void sendNotification(String title,
                                  String message,
                                  String receiver,
                                  String receiverUid,
                                  String firebaseToken) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_messaging)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
