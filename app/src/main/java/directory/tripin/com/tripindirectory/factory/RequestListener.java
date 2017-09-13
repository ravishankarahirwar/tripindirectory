package directory.tripin.com.tripindirectory.factory;


/**
 * @author Yogesh Tikam
 * @since 12/09/2017.
 */

public interface RequestListener extends ResponseResults {
    /**
     * @param responseResult There is two possible value of response CONNECTION_ERROR & RESPONSE_OK
     *                       if request not reached at server we return CONNECTION_ERROR otherwise return RESPONSE_OK.
     * @param response       This is the response object we get it from Server side.
     */
    void onResponse(int responseResult, int apiTag, Response response);
    void onError(int responseResult, int apiTag, String message, Response response);

}
