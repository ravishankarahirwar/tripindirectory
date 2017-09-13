package directory.tripin.com.tripindirectory.factory;

/**
 * @author Yogesh Tikam
 * @since 12/09/2017
 */

public class ConnectionFactory extends ConnectRequest {

/*
    public ConnectionFactory(Context context,
                             RequestListener connectionListener) {
        super();
        mConnectionListener = connectionListener;
        this.mContext = context;
    }

    public void setPostParams(Map<String, String> postParams) {
        Log.v("Partner", "postParams=" + postParams);
        this.mPostParams = postParams;
    }

    public void createConnection(int tag) {
        if (NetworkUtils.isNetworkConnectionAvailable(mContext)) {
            Connector connector = null;
//            connector = new RequestManager(mContext, mConnectionListener, ApiProvider.getApiByTag(tag), tag);
            if (mPostParams != null) {
                connector = new RequestManager(mContext, mConnectionListener, ApiProvider.getApiByTag(tag), tag);
                connector.setPostParams(mPostParams);
            }
            connector.connect();
        } else {
            NetworkUtils.showNoInternetDialog(mContext);
            mConnectionListener.onError(ResponseResults.NO_INTERNET, tag, mContext.getString(R.string.error_no_internet), null);
        }
    }*/
}
