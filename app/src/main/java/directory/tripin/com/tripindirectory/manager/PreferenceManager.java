package directory.tripin.com.tripindirectory.manager;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yogesh Tikam on 5/10/2017.
 */

public class PreferenceManager {
    public static final String PREF_FCM_TOKEN = "fcmtoken";
    public static final String PREF_EMAIL = "useremail";

    public static final String PREF_FACEBOOK_UID = "fuid";
    public static final String PREF_IS_MAINSCREEN_GUIDED = "ismainguided";
    public static final String PREF_IS_LOADBOARD_GUIDED = "isloadboardguided";
    public static final String PREF_IS_POSTTOSELECTED_GUIDED = "isposttoselectedguided";
    public static final String PREF_IS_MAININTRO_SEEN = "ismainintroseen";
    public static final String PREF_IS_PROFILEINTRO_SEEN = "isprofileintroseen";





    public static final String PREF_DISPLAY_NAME = "displayneame";
    public static final String PREF_COMPANY_NAME = "companyname";
    public static final String PREF_IMAGE_URL = "imageurl";
    public static final String PREF_FACEBOOKED = "isfacebooked";
    public static final String PREF_REG_MOBILE = "mobile";
    public static final String PREF_IS_ON_NEWLOOK = "isonnewlook";
    public static final String PREF_IS_INBOXREAD = "isinboxread";

    public static final String PREF_TO_SHOW_REG_AD = "toshowregad";

    public static final String PREF_ISNEWLOOKACCEPTED = "isnewlookaccepted";



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



    public void setisFacebboked(boolean b) {
        editor.putBoolean(PREF_FACEBOOKED, b);
        editor.commit();
    }

    public boolean isFacebooked() {
        return sInstance.getBoolean(PREF_FACEBOOKED, false);
    }



    public boolean toShowRegAd() {
        return sInstance.getBoolean(PREF_TO_SHOW_REG_AD, true);
    }

    public void setToShowRegAd(boolean b) {
        editor.putBoolean(PREF_TO_SHOW_REG_AD, b);
        editor.commit();
    }

    public boolean isInboxRead() {
        return sInstance.getBoolean(PREF_IS_INBOXREAD, true);
    }
    public void setInbocRead(boolean b) {
        editor.putBoolean(PREF_IS_INBOXREAD, b);
        editor.commit();
    }

    public boolean isOnNewLook() {
         return sInstance.getBoolean(PREF_IS_ON_NEWLOOK, true);
    }
    public void setisOnNewLook(boolean b) {
        editor.putBoolean(PREF_IS_ON_NEWLOOK, b);
        editor.commit();
    }

    public void setisNewLookAccepted(boolean b) {
        editor.putBoolean(PREF_ISNEWLOOKACCEPTED, b);
        editor.commit();
    }

    public boolean isNewLookAccepted() {
        return sInstance.getBoolean(PREF_ISNEWLOOKACCEPTED, true);
    }

    public void setisMainScreenGuided(boolean b) {
        editor.putBoolean(PREF_IS_MAINSCREEN_GUIDED, b);
        editor.commit();
    }

    public boolean isMainScreenGuided() {
        return sInstance.getBoolean(PREF_IS_MAINSCREEN_GUIDED, false);
    }

    public void setisLBScreenGuided(boolean b) {
        editor.putBoolean(PREF_IS_LOADBOARD_GUIDED, b);
        editor.commit();
    }

    public boolean isLBScreenGuided() {
        return sInstance.getBoolean(PREF_IS_LOADBOARD_GUIDED, false);
    }

    public void setisPTSScreenGuided(boolean b) {
        editor.putBoolean(PREF_IS_POSTTOSELECTED_GUIDED, b);
        editor.commit();
    }

    public boolean isPTSScreenGuided() {
        return sInstance.getBoolean(PREF_IS_POSTTOSELECTED_GUIDED, false);
    }

    public boolean isMainIntroSeen() {
        return sInstance.getBoolean(PREF_IS_MAININTRO_SEEN, false);
    }

    public void setisMainIntroSeen(boolean b) {
        editor.putBoolean(PREF_IS_MAININTRO_SEEN, b);
        editor.commit();
    }

    public boolean isProfileIntroSeen() {
        return sInstance.getBoolean(PREF_IS_PROFILEINTRO_SEEN, false);
    }

    public void setisProfileIntroSeen(boolean b) {
        editor.putBoolean(PREF_IS_PROFILEINTRO_SEEN, b);
        editor.commit();
    }

    public String getEmail() {
        String token = sInstance.getString(PREF_EMAIL, "");
        return token;
    }

    public void setEmail(String accessToken) {
        editor.putString(PREF_EMAIL, accessToken);
        editor.commit();
    }

    public String getFcmToken() {
        String token = sInstance.getString(PREF_FCM_TOKEN, null);
        return token;
    }

    public void setFcmToken(String accessToken) {
        editor.putString(PREF_FCM_TOKEN, accessToken);
        editor.commit();
    }

    public String getRMN() {
        String token = sInstance.getString(PREF_REG_MOBILE, "");
        return token;
    }

    public void setRMN(String rmn) {
        editor.putString(PREF_REG_MOBILE, rmn);
        editor.commit();
    }

    public String getFuid() {
        String fuid = sInstance.getString(PREF_FACEBOOK_UID, "");
        return fuid;
    }

    public void setFuid(String fuid) {
        editor.putString(PREF_FACEBOOK_UID, fuid);
        editor.commit();
    }

    public String getImageUrl() {
        String url = sInstance.getString(PREF_IMAGE_URL, null);
        return url;
    }

    public void setImageUrl(String url) {
        editor.putString(PREF_IMAGE_URL, url);
        editor.commit();
    }

    public String getDisplayName() {
        String name = sInstance.getString(PREF_DISPLAY_NAME, null);
        return name;
    }

    public void setDisplayName(String name) {
        editor.putString(PREF_DISPLAY_NAME, name);
        editor.commit();
    }

    public String getComapanyName() {
        String name = sInstance.getString(PREF_COMPANY_NAME, null);
        return name;
    }

    public void setCompanyName(String name) {
        editor.putString(PREF_COMPANY_NAME, name);
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
