package directory.tripin.com.tripindirectory.dataprovider;

import com.android.volley.DefaultRetryPolicy;

/**
 * Created by Chandni Patel
 */

public class ProviderUtils {
    private static final int API_RETRY_TIME = 60000;
    public static final String BODY_CONTENT_TYPE = "application/json";

public static DefaultRetryPolicy getRetryPolicy() {
    return new DefaultRetryPolicy(API_RETRY_TIME,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
}
}

