package directory.tripin.com.tripindirectory.factory;

/**
 * @author Yogesh Tikam
 * @since 12/09/2017.
 */


import java.util.Map;

public interface Connector {
     void setPostParams(Map<String, String> postParams);
     void setHeaderParams(Map<String, String> postParams);

     void connect();
     void parseJson(String response);
}
