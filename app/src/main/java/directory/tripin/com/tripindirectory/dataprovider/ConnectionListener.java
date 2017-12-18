package directory.tripin.com.tripindirectory.dataprovider;


import directory.tripin.com.tripindirectory.factory.Response;
import directory.tripin.com.tripindirectory.factory.ResponseResults;

/**
 * @author Chandni Patel
 * @since 12/2/2016.
 */

public interface ConnectionListener extends ResponseResults {
    /**
     * @param responseResult There is two possible value of response CONNECTION_ERROR & RESPONSE_OK
     *                       if request not reached at server we return CONNECTION_ERROR otherwise return RESPONSE_OK.
     * @param response       This is the response object we get it from Server side.
     */
    void onResponse(int responseResult, Response response);
    void onError(int responseResult, int apiTag, String message, Response response);

}
