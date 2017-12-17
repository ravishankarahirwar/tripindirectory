package directory.tripin.com.tripindirectory.dataprovider;

/**
 * Created by Yogesh N. Tikam on 12/09/2017.
 *
 */

public class ApiTag {

    public static final int GET_PARTNERS = 1;

    public static final int ELASTIC_SEARCH = 2;

    public static final int GET_TOKEN = 3;

    public static final int LIKE_DISLIKE = 4;

    /**
     * The following API Tag is for Google Place API Autocomplete Service.
     * Please note that this Api is not documented in our Tripin Api document.
     * It is a third party Api which is used to autocomplete the written suggestions for any place name entered.
     * However we use this Api in our application to filter out choices of city as per the state name provided by the user.
     * This Api represents a GET Request to the server where there are 2 parameters to be sent.
     * First parameter is input . In our app, the value will be the city initials entered by the user.
     * The second parameter is the  Key which is sent while the URL is actually creating connection. It's value is the API key provided by Google to us.
     */
    public static final int GOOGLE_API_FOR_CITY = 5;

}

