package directory.tripin.com.tripindirectory.interfaces;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * @author  Ravishankar
 * @since 05-01-2017.
 */

public interface RouteFinderListner {
    void onRouteFind(ArrayList<LatLng> points);
    void onNoRouteFind();
    void onDistanceFind(String distance);
    void onEstimatedTimeFind(String time);
}
