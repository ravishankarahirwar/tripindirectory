package directory.tripin.com.tripindirectory.manager;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yogesh Tikam on 5/10/2017.
 */

public class PreferenceManager {
    public static final String PREF_TOKEN = "token";
    public static final String DEVICE_ID = "device_id";

    public static final String PREF_GROUP_ID = "group_id";
    public static final String PREF_FIRST_TIME = "first_time";
    private static final String PREF_FILE_NAME = "tripin_directory";
    private static SharedPreferences sInstance;
    private static SharedPreferences.Editor editor;
    private static PreferenceManager mSPreferenceManager;

    private PreferenceManager() {
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

    public String getToken() {
        String token = sInstance.getString(PREF_TOKEN, null);
        return token;
    }

    public void setToken(String accessToken) {
        editor.putString(PREF_TOKEN, accessToken);
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
}