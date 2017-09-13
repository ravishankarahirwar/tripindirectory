package directory.tripin.com.tripindirectory.factory;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * @author Yogesh Tikam
 * @author 12/09/2017.
 */

public class RequestPool {
    private static RequestPool sInstance;
    private static Context sContext;
    private RequestQueue mRequestQueue;

    private RequestPool(Context context) {
        sContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestPool getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RequestPool(context);
        }
        return sInstance;
    }

    public void cancellAllPreviousRequestWithSameTag(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(sContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

