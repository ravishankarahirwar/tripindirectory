package directory.tripin.com.tripindirectory.helper;

import android.animation.TypeEvaluator;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-01-2017
 */

public class RouteEvaluator implements TypeEvaluator<LatLng> {
    @Override
    public LatLng evaluate(float t, LatLng startPoint, LatLng endPoint) {
        double lat = startPoint.latitude + t * (endPoint.latitude - startPoint.latitude);
        double lng = startPoint.longitude + t * (endPoint.longitude - startPoint.longitude);
        return new LatLng(lat,lng);
    }
}