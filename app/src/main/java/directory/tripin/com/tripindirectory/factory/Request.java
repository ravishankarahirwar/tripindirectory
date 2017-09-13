package directory.tripin.com.tripindirectory.factory;

import android.content.Context;


import java.util.Map;

import directory.tripin.com.tripindirectory.volley.DataModel;

/**
 * @author Yogesh Tikam
 * @version 1.0
 * @since 12/09/2017
 */

public class Request extends ConnectRequest {

    private final Context mContext;
    private final int mConnectionType;
    private final String mRequestUrl;
    private final String mRawData;
    private final int mTag;
    private final Map<String, String> mPostParams;
    private final Map<String, String> mHeaderParams;
    private final Map<String, DataModel> mByteParams;
    private final RequestListener mConnectionListener;
    public Request(RequestBuilder requestBuilder) {
        mContext = requestBuilder.mContext;
        mRequestUrl = requestBuilder.mRequestUrl;
        mRawData = requestBuilder.mRawData;
        mTag = requestBuilder.mTag;
        mConnectionType = requestBuilder.mConnectionType;
        mPostParams = requestBuilder.mPostParams;
        mByteParams = requestBuilder.mByteParams;
        mHeaderParams = requestBuilder.mHeaderParams;
        mConnectionListener = requestBuilder.mConnectionListener;
    }

    public Context getContext() {
        return mContext;
    }

    public int getTag() {
        return mTag;
    }

    public int getType() {
        return mConnectionType;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public String getRawData() {
        return mRawData;
    }

    public Map<String, String> getPostParams() {
        return mPostParams;
    }

    public Map<String, String> getHeaderParams() {
        return mHeaderParams;
    }

    public RequestListener getConnectionListener() {
        return mConnectionListener;
    }

    public Map<String, DataModel> getByteParams() {
        return mByteParams;
    }

    /**
     * Supported request methods.
     */
    public interface Method {
        int DEPRECATED_GET_OR_POST = -1;
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
        int MULTIPART = 8;
        int SEND_JSON = 9;

    }

    public static class RequestBuilder {
        private final Context mContext; //Required
        private final RequestListener mConnectionListener; //Required

        private int mTag;
        private int mConnectionType;
        private String mRequestUrl;
        private String mRawData;
        private Map<String, String> mPostParams;
        private Map<String, String> mHeaderParams;
        private Map<String, DataModel> mByteParams;

        public RequestBuilder(Context context, RequestListener requestListener) {
            this.mContext = context;
            this.mConnectionListener = requestListener;
        }

        public RequestBuilder type(int type) {
            this.mConnectionType = type;
            return this;
        }

        public RequestBuilder tag(int tag) {
            this.mTag = tag;
            return this;
        }

        public RequestBuilder url(String url) {
            this.mRequestUrl = url;
            return this;
        }

        public RequestBuilder rawData(String rawData) {
            this.mRawData = rawData;
            return this;
        }

        public RequestBuilder postParams(Map<String, String> postParams) {
            this.mPostParams = postParams;
            return this;
        }

        public RequestBuilder headerParams(Map<String, String> headerParams) {
            this.mHeaderParams = headerParams;
            return this;
        }

        public RequestBuilder byteParams(Map<String, DataModel> byteParams) {
            this.mByteParams = byteParams;
            return this;
        }

        public Request build() {
            return new Request(this);
        }

    }
}
