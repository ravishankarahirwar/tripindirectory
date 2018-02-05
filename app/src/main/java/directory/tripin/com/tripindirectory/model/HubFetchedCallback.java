package directory.tripin.com.tripindirectory.model;

/**
 * Created by Shubham on 2/3/2018.
 */

public interface HubFetchedCallback {
    public void onDestinationHubFetched(String destinationhub, int operaion);
    public void onSourceHubFetched(String sourcehub, int operation);
}
