package directory.tripin.com.tripindirectory.dataprovider;


/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 */
public class ApiProvider {
    //    private static final String BASE_URL = "http://dev.tripininc.in:5000/api/";
    private static final String BASE_URL = "http://139.59.70.142:8002/api/v1/";


    public static String getApiByTag(int apiTag) {
        String url;

        switch (apiTag) {
            case ApiTag.GET_PARTNERS: // 1
                url = BASE_URL + "getPartner";
                break;

            case ApiTag.ELASTIC_SEARCH: // 2
                url = BASE_URL + "elastic/searchDirectory";
                break;

            case ApiTag.GET_TOKEN: // 3
                url = BASE_URL + "person/addPersonByDeviceID";
                break;

            case ApiTag.LIKE_DISLIKE: // 4
                url = BASE_URL + "org/vote";
                break;

            default:
                return null;
        }
        return url;
    }
}

