package directory.tripin.com.tripindirectory.chat.core.chat;

import android.content.Context;
import android.util.Log;


import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import directory.tripin.com.tripindirectory.chat.fcm.FcmNotificationBuilder;
import directory.tripin.com.tripindirectory.chat.models.Chat;
import directory.tripin.com.tripindirectory.chat.utils.Constants;
import directory.tripin.com.tripindirectory.chat.utils.SharedPrefUtil;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;


public class ChatInteractor implements ChatContract.Interactor {
    private static final String TAG = "ChatInteractor";

    private ChatContract.OnSendMessageListener mOnSendMessageListener;
    private ChatContract.OnGetMessagesListener mOnGetMessagesListener;

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener) {
        this.mOnSendMessageListener = onSendMessageListener;
    }

    public ChatInteractor(ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

    public ChatInteractor(ChatContract.OnSendMessageListener onSendMessageListener,
                          ChatContract.OnGetMessagesListener onGetMessagesListener) {
        this.mOnSendMessageListener = onSendMessageListener;
        this.mOnGetMessagesListener = onGetMessagesListener;
    }

        public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken, String newNull) {
        final String room_type_1 = chat.senderUid + "_" + chat.receiverUid;
        final String room_type_2 = chat.receiverUid + "_" + chat.senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_1 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "sendMessageToFirebaseUser: " + room_type_2 + " exists");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_2).child(String.valueOf(chat.timestamp)).setValue(chat);
                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success");
                    databaseReference.child(Constants.ARG_CHAT_ROOMS).child(room_type_1).child(String.valueOf(chat.timestamp)).setValue(chat);
                    getMessageFromFirebaseUser(chat.senderUid, chat.receiverUid);
                }

                // send push notification to the receiver
                sendPushNotificationToReceiver(chat.sender,
                        chat.message,
                        chat.senderUid,
                        PreferenceManager.getInstance(context).getFcmToken(),
                        receiverFirebaseToken);
                mOnSendMessageListener.onSendMessageSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnSendMessageListener.onSendMessageFailure("Unable to send message: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void sendMessageToFirebaseUser(final Context context, final Chat chat, final String receiverFirebaseToken) {

        String senderId = chat.getSenderUid();
        String receiverId = chat.getReceiverUid();
        String userId = FirebaseAuth.getInstance().getUid();

        if (senderId.equals(userId)){

            DatabaseReference senderChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_ROOMS);
            DatabaseReference receiverChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_ROOMS);

            DatabaseReference senderChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_LIST);
            DatabaseReference receiverChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_LIST);

            senderChat.child(receiverId).child(String.valueOf(chat.getTimestamp())).setValue(chat);
            receiverChat.child(senderId).child(String.valueOf(chat.getTimestamp())).setValue(chat);

            chat.setType(1);
            senderChatList.child(receiverId).setValue(chat);
            receiverChatList.child(senderId).setValue(chat);

        } else {

            DatabaseReference senderChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_ROOMS);
            DatabaseReference receiverChat = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_ROOMS);

            DatabaseReference senderChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(senderId).child(Constants.ARG_CHAT_LIST);
            DatabaseReference receiverChatList = FirebaseDatabase.getInstance().getReference(Constants.ARG_USERS).child(receiverId).child(Constants.ARG_CHAT_LIST);

            senderChat.child(senderId).child(String.valueOf(chat.getTimestamp())).setValue(chat);
            receiverChat.child(receiverId).child(String.valueOf(chat.getTimestamp())).setValue(chat);

            senderChatList.child(senderId).setValue(chat);
            receiverChatList.child(receiverId).setValue(chat);

        }

        //getMessageFromFirebaseUser(chat.getReporterUid(), chat.getReportedUid());

        // send push notification to the receiver

        if (chat.getType() == 1){

            sendPushNotificationToReceiver(
                    chat.getSender(),
                    chat.getMessage(),
                    chat.getSenderUid(),
                    new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                    receiverFirebaseToken);


        } else if (chat.getType() == 2){

            sendPushNotificationToReceiver(
                    chat.getSender(),
                    "sent you an audio",
                    chat.getSenderUid(),
                    new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                    receiverFirebaseToken);

        } else if (chat.getType() == 3){

            sendPushNotificationToReceiver(
                    chat.getSender(),
                    "sent you an image",
                    chat.getSenderUid(),
                    new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN),
                    receiverFirebaseToken);

        }


        mOnSendMessageListener.onSendMessageSuccess();

    }

    private void sendPushNotificationToReceiver(String username,
                                                String message,
                                                String uid,
                                                String firebaseToken,
                                                String receiverFirebaseToken) {
        FcmNotificationBuilder.initialize()
                .title(username)
                .message(message)
                .username(username)
                .uid(uid)
                .firebaseToken(firebaseToken)
                .receiverFirebaseToken(receiverFirebaseToken)
                .send();
    }

    @Override
    public void getMessageFromFirebaseUser(String senderUid, String receiverUid) {
        final String room_type_1 = senderUid + "_" + receiverUid;
        final String room_type_2 = receiverUid + "_" + senderUid;

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(Constants.ARG_CHAT_ROOMS).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(room_type_1)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_1 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else if (dataSnapshot.hasChild(room_type_2)) {
                    Log.e(TAG, "getMessageFromFirebaseUser: " + room_type_2 + " exists");
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(Constants.ARG_CHAT_ROOMS)
                            .child(room_type_2).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Chat chat = dataSnapshot.getValue(Chat.class);
                            mOnGetMessagesListener.onGetMessagesSuccess(chat);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "getMessageFromFirebaseUser: no such room available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mOnGetMessagesListener.onGetMessagesFailure("Unable to get message: " + databaseError.getMessage());
            }
        });
    }
}
