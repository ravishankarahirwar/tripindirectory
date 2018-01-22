package directory.tripin.com.tripindirectory.manager;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yogesh Tikam on 5/10/2017.
 */

public class PreferenceManager {
    public static final String PREF_FCM_TOKEN = "fcmtoken";
    public static final String DEVICE_ID = "device_id";
    public static final String USER_ID = "user_id";

    public static final String PREF_GROUP_ID = "group_id";
    public static final String PREF_FIRST_TIME = "first_time";
    private static final String PREF_FILE_NAME = "tripin_directory";
    private static final String PREF_GOT_AUTOSYNC = "got_autosync";

    private static SharedPreferences sInstance;
    private static SharedPreferences.Editor editor;
    private static PreferenceManager mSPreferenceManager;
    public boolean isAutoSyncGot;

    public PreferenceManager() {
    }

    /**
     * @param context
     * @return
     */
    public static synchronized PreferenceManager getInstance(Context context) {
        if (mSPreferenceManager == null) {
//            sInstance = PreferenceManager.getDefaultSharedPreferences(context);
            sInstance = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            editor = sInstance.edit();
            mSPreferenceManager = new PreferenceManager();
        }
        return mSPreferenceManager;
    }

    public String getValueFromSharedPreference(String key, String value) {
        String retrivedValue = sInstance.getString(key, value);
        return retrivedValue;
    }

    public String getFcmToken() {
        String token = sInstance.getString(PREF_FCM_TOKEN, null);
        return token;
    }

    public void setFcmToken(String accessToken) {
        editor.putString(PREF_FCM_TOKEN, accessToken);
        editor.commit();
    }

    public String getDeviceId() {
        String deviceId = sInstance.getString(DEVICE_ID, null);
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        editor.putString(DEVICE_ID, deviceId);
        editor.commit();
    }

    public String getUserId() {
        String userId = sInstance.getString(USER_ID, null);
        return userId;
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }


    public String getGroupId() {
        String groupId = sInstance.getString(PREF_GROUP_ID, null);
        return groupId;
    }

    public void setGroupId(String groupId) {
        editor.putString(PREF_GROUP_ID, groupId);
        editor.commit();
    }

    public boolean isFirstTime() {
        boolean firstTime = sInstance.getBoolean(PREF_FIRST_TIME, true);
        return firstTime;
    }

    public void setFirstTime(boolean isSecondTime) {
        editor.putBoolean(PREF_FIRST_TIME, isSecondTime);
        editor.commit();
    }


    public String getValueFromSharedPreference(String key) {
        return getValueFromSharedPreference(key, "");
    }

    public void setValueToSharedPreference(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void clearAllDataFromSharedPrefernces() {
        editor.clear();
        editor.commit();
    }

    public void setIsAutoSyncGot(boolean isAutoSyncGot) {
        editor.putBoolean(PREF_GOT_AUTOSYNC, isAutoSyncGot);
        editor.commit();
    }
    public boolean IsAutoSyncGot() {
        boolean firstTime = sInstance.getBoolean(PREF_GOT_AUTOSYNC, false);
        return firstTime;
    }
}
