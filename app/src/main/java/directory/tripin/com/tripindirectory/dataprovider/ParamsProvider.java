package directory.tripin.com.tripindirectory.dataprovider;

import android.content.ContentValues;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 *
 */
public class ParamsProvider {

    public static Map<String, String> getParams(int apiTag) {
        switch (apiTag) {
            case ApiTag.GET_PARTNERS: // Api tag no. 1
                Map<String, String> params = new HashMap<String, String>();
                params.put("client_id", "testclient");
                params.put("grant_type", "client_credentials");
                params.put("client_secret", "testpass");
                return params;
        }
        return null;
    }

    public static Map<String, String> getParams(ContentValues contentValues) {
        if (contentValues.size() > 0) {
            Map<String, String> params = new HashMap<String, String>();
            for (String key : contentValues.keySet()) {
                params.put(key, contentValues.getAsString(key));
            }
            return params;
        } else {
            return null;
        }
    }

    public static Map<String, String> getHeaderParams() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        return headers;
    }
}
