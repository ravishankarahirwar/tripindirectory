package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 2/3/2018.
 */

public interface HubFetchedCallback {
    void onDestinationHubFetched(String destinationhub, int operaion);
    void onSourceHubFetched(String sourcehub, int operation);
}