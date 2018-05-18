package directory.tripin.com.tripindirectory.chat.ui.viewholder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vanniktech.emoji.EmojiTextView;

import de.hdodenhof.circleimageview.CircleImageView;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.TripinDirectoryApp;
import directory.tripin.com.tripindirectory.chat.ui.activities.ChatActivity;
import directory.tripin.com.tripindirectory.chat.utils.Constants;
import directory.tripin.com.tripindirectory.utils.GlideUtil;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class ListChatViewHolder extends RecyclerView.ViewHolder {
    private final View mView;
    public DatabaseReference mUsersRef;
    public ValueEventListener mUserListener;

    private Dialog progressDialog;

    private static final int POST_TEXT_MAX_LINES = 6;

    private CircleImageView profileImage;
    private ImageView online;
    private TextView name;
    private TextView body;
    private TextView timeChat;
    private ImageView status;
    private RelativeLayout messageView;
    private RelativeLayout RView;

    public String mPostKey;
    public ValueEventListener mLikeListener;
    private String mUserId;
    public ListChatViewHolder(View itemView) {
        super(itemView);
        mView = itemView;

        RView = mView.findViewById(R.id.rViewe);
        profileImage =  mView.findViewById(R.id.profile_photo);
        name =  mView.findViewById(R.id.username);
        body =  mView.findViewById(R.id.message_txt);
        timeChat =  mView.findViewById(R.id.time);
        status =  mView.findViewById(R.id.imageView6);
        online =  mView.findViewById(R.id.image_online_status);
        messageView =  mView.findViewById(R.id.list);
        mUserId = FirebaseAuth.getInstance().getUid();
    }

    public void setPhoto(String url) {
        GlideUtil.loadImage(url, profileImage);


        profileImage.setOnClickListener(view -> {

            if (url != null){

                ShowImageView(url);
            } else {

                Toast.makeText(TripinDirectoryApp.getInstance().getApplicationContext(), "No profile picture to show", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setTime (String time){

        if (time == null || time.isEmpty()){
            time = mView.getResources().getString(R.string.no_location);
        }

        timeChat.setText(time);

    }

    public void setLastMessage (String lastMessage){

        if (lastMessage == null || lastMessage.isEmpty()){
            lastMessage = mView.getResources().getString(R.string.no_location);
        }

        body.setText(lastMessage);

    }

    public void setOnline(String url) {
        GlideUtil.checkOnline(url, online);
    }

    public void setOffline(String url) {
        GlideUtil.checkOffline(url, online);
    }

    public void setSoon(String url) {
        GlideUtil.checkSoon(url, online);
    }



    public void setRead() {

        status.setImageResource(R.drawable.ic_read_24dp);
    }

    public void setUnRead() {

        status.setImageResource(R.drawable.ic_sent_24dp);
    }

    public void sethideStatus() {

        status.setVisibility(View.GONE);
    }

    public void setLastMessageBold (String lastMessage){

        if (lastMessage == null || lastMessage.isEmpty()){
            lastMessage = mView.getResources().getString(R.string.no_location);
        }


        SpannableString span2 = new SpannableString(lastMessage);
        span2.setSpan(new StyleSpan(Typeface.BOLD),0,lastMessage.length(),SPAN_INCLUSIVE_INCLUSIVE); // set Style

        // let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span2);

        body.setText(finalText);
        body.setTextColor(Color.RED);

    }


    public void setUser(String users, final String selectedUserId, final String getFcmUserDeviceId) {
        if (users == null || users.isEmpty()) {
            users = mView.getResources().getString(R.string.user_info_no_name);
        }
        name.setText(users);


        String finalUsers = users;

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showUserDetail(finalUsers, selectedUserId, getFcmUserDeviceId);

            }
        });


        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(finalUsers, selectedUserId, getFcmUserDeviceId);
            }
        });

        messageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                RView.setBackgroundResource(R.color.gray1);

                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                builder.setTitle("Delete this conversation ?");
                builder.setCancelable(false);
                // add a list
                String[] animals = {"Delete", "Cancel"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                // setup the alert builder
                                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
                                builder.setTitle("This will only delete your copy");
                                builder.setCancelable(false);
                                // add a list
                                String[] animals = {"Delete", "Cancel"};
                                builder.setItems(animals, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:

                                                showProgressBar("Deleting...");
                                                DatabaseReference chat = FirebaseDatabase.getInstance().getReference()
                                                        .child(Constants.ARG_USERS) // UsersGlideUtil
                                                        .child(mUserId) // Me
                                                        .child(Constants.ARG_CHAT_ROOMS) // Chat
                                                        .child(selectedUserId); // Chat ID

                                                chat.removeValue((databaseError, databaseReference) -> {

                                                    DatabaseReference chatList = FirebaseDatabase.getInstance().getReference()
                                                            .child(Constants.ARG_USERS) // Users
                                                            .child(mUserId) // Me
                                                            .child(Constants.ARG_CHAT_LIST) // Chat
                                                            .child(selectedUserId); // Chat List ID

                                                    chatList.removeValue((databaseError1, databaseReference1) -> {

                                                        dismissProgressBar();
                                                        RView.setBackgroundResource(R.color.transparent);

                                                        Toast.makeText(mView.getContext(), "Conversation with" + " " + "with" + finalUsers + " " + "deleted.", Toast.LENGTH_LONG).show();
                                                    });


                                                });

                                            case 1:
                                                RView.setBackgroundResource(R.color.transparent);

                                        }
                                    }
                                });

                                // create and show the alert dialog
                                AlertDialog dialog2 = builder.create();
                                dialog2.show();



                            case 1:
                                RView.setBackgroundResource(R.color.transparent);
                        }
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
    }

    private void showUserDetail(String getName, String selectedUserId, String getFcmUserDeviceId) {
        Context context = mView.getContext();

        ChatActivity.startActivity(context, getName, selectedUserId, getFcmUserDeviceId);

    }

    public void ShowImageView(String imageUrl){

//        Intent imageViewerIntent = new Intent(TripinDirectoryApp.getInstance().getApplicationContext(), ImageViewerActivity.class);
//        imageViewerIntent.putExtra(ImageViewerActivity.EXTRA_IMAGE_URL, imageUrl);
//        TripinDirectoryApp.getInstance().getApplicationContext().startActivity(imageViewerIntent);
    }

    public void showProgressBar(String message){
        progressDialog = ProgressDialog.show(mView.getContext(), "", message, true);
    }
    public void dismissProgressBar() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}