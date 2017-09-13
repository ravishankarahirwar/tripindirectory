package directory.tripin.com.tripindirectory.factory;

import android.content.Context;

import java.util.Map;


/**
 * @author  Yogesh Tikam
 * @since 12/09/2017.
 */

public class ConnectRequest {
    protected Context mContext;
    protected String mRawData;
    protected Map<String, String> mPostParams;
    protected Map<String, String> mHeaderParams;
    protected RequestListener mConnectionListener;
}
