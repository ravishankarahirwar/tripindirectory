package directory.tripin.com.tripindirectory.manager;

import com.google.android.gms.maps.model.LatLng;

import directory.tripin.com.tripindirectory.interfaces.RouteFinderListner;


/**
 * @author RavishankarAhirwar
 * @version 1.0
 * @since 05/01/2017
 */

public class RouteManager {
    private static final String ROUTE_URL = "https://maps.googleapis.com/maps/api/directions/";
    private static RouteManager sInstance;
    private RouteFinderListner mRouteFinderListner;
    private RuteFindingTask mRuteFindingTask;


    public RouteManager(LatLng souce, LatLng dest, RouteFinderListner routeFinderListner) {
        this.mRouteFinderListner = routeFinderListner;
        String url = getUrl(souce, dest);
        mRuteFindingTask = new RuteFindingTask(url, mRouteFinderListner);
    }

    /**
     * @param origin
     * @param dest
     * @return
     */
    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = ROUTE_URL + output + "?" + parameters;

        return url;
    }
}
