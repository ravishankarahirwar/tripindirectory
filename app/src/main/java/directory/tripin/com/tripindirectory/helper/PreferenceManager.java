package directory.tripin.com.tripindirectory.helper;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Yogesh Tikam on 12/09/2017.
 */

public class PreferenceManager {
    private static final String PREF_FILE_NAME = "directory_preference";

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
