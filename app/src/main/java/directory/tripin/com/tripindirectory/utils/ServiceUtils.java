package directory.tripin.com.tripindirectory.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.firebase.database.FirebaseDatabase;

import directory.tripin.com.tripindirectory.Messaging.Class.User;


public class ServiceUtils {

    public static long TIME_TO_REFRESH = 55 * 1000;
    public static long TIME_TO_SOON = 60 * 1000;
    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;

    public static void updateUserStatus(Context context){
        if(isNetworkConnected(context)) {


            if (User.getCurrentUserId() != null) {
                FirebaseDatabase.getInstance().getReference().child("users/" + User.getCurrentUserId() + "/isOnline").setValue(true);
                FirebaseDatabase.getInstance().getReference().child("users/" + User.getCurrentUserId() + "/timestamp").setValue(System.currentTimeMillis());
            }
        }
    }


    public static boolean isNetworkConnected(Context context) {
        try{
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }catch (Exception e){
            return true;
        }
    }
}
