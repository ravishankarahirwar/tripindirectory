package directory.tripin.com.tripindirectory.manager;

import android.content.Context;

import directory.tripin.com.tripindirectory.Communication.RquestHandler;
import directory.tripin.com.tripindirectory.dataprovider.ApiTag;
import directory.tripin.com.tripindirectory.dataprovider.RequestProvider;
import directory.tripin.com.tripindirectory.factory.RequestListener;
import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.model.response.TokenResponse;
import directory.tripin.com.tripindirectory.role.ITokenOptions;


/**
 * Created by Yogesh N. Tikam on 5/10/2017.
 */

public class TokenManager implements RequestListener, ITokenOptions {

    private Context mContext;

    private RequestProvider mRequestProvider;

    private TokenListener mTokenListener;


    public TokenManager(Context context) {
        this.mContext = context;
        this.mRequestProvider = new RequestProvider(mContext);
    }

    public void getCurrentToken(TokenListener tokenListenerCallback, String deviceId) {
        this.mTokenListener = tokenListenerCallback;
        getToken(deviceId);

    }

    @Override
    public void getToken(String deviceId) {
        RquestHandler rquestHandler = new RquestHandler(mContext);
        rquestHandler.send(mRequestProvider.getTokenRequest(deviceId,this));
    }

    @Override
    public void onResponse(int responseResult, int apiTag, Response response) {
        switch (apiTag) {
            case ApiTag.GET_TOKEN:
                mTokenListener.onSuccess((TokenResponse) response);
                break;
        }
    }

    @Override
    public void onError(int responseResult, int apiTag, String message, Response response) {
        switch (apiTag) {
            case ApiTag.GET_TOKEN:
                mTokenListener.onFailed(message);
                break;
        }
    }

    public interface TokenListener {

        void onSuccess(TokenResponse tokenResponse);

        void onFailed(String message);
    }

}
