package directory.tripin.com.tripindirectory.dataprovider;

/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 *
 */
public class ApiProvider {
    private static final String BASE_URL = "http://dev.tripininc.in:5000/api/";

    public static String getApiByTag(int apiTag) {
        String url;

        switch (apiTag) {

            case ApiTag.GET_PARTNERS: // 1
                url = BASE_URL + "getPartner";
                break;

            default:
                return null;
        }
        return url;
    }
}

