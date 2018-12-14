package directory.tripin.com.tripindirectory.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Shubham on 2/3/2018.
 */

public class RouteCityPojo {

    private String mCityName = "";
    private LatLng mLatLang = null;
    private FoundHubPojo mNearestHub = null;
    private Context mContext;
    private List<FoundHubPojo> mNearestHubsList;
    private int mRadiusMultiplier;
    private static final int GIOQUERY_RADIUS = 50;
    private boolean isFetchingHub = false;
    private HubFetchedCallback hubFetchedCallback = null;
    private int mCityType = 0;
    private int mOperationTag = 0;


    public RouteCityPojo(Context context) {
        mContext = context;
        mNearestHubsList = new ArrayList<>();
    }
    public RouteCityPojo(Context context,int mCityType,int mOperationTag, HubFetchedCallback hubFetchedCallback) {
        mContext = context;
        mNearestHubsList = new ArrayList<>();
        this.hubFetchedCallback = hubFetchedCallback;
        this.mCityType = mCityType;
        this.mOperationTag = mOperationTag;
    }

    public boolean isFetchingHub() {
        return isFetchingHub;
    }

    public void setFetchingHub(boolean fetchingHub) {
        isFetchingHub = fetchingHub;
    }

    public String getmCityName() {
        return mCityName;
    }

    public void setmCityName(String mCityName) {
        this.mCityName = mCityName;
        setmLatLang(getLocationFromAddress(mContext, mCityName));
    }

    public LatLng getmLatLang() {
        return mLatLang;
    }

    public void setmLatLang(LatLng mLatLang) {
        this.mLatLang = mLatLang;
        directory.tripin.com.tripindirectory.helper.Logger.v("LatLong: " + mCityName + " " + mLatLang.latitude + "," + mLatLang.longitude);
        if(mLatLang!=null){
            fetchNearestHub(mLatLang);
        }
    }

    private void fetchNearestHub(final LatLng mLatLang) {
        // run geo query
        mRadiusMultiplier = 1;
        isFetchingHub = true;
        DatabaseReference mGeoRef;
        mGeoRef = FirebaseDatabase.getInstance().getReference("hubsgeofire");
        GeoFire geoFire;
        geoFire = new GeoFire(mGeoRef);
        // creates a new query around [37.7832, -122.4056] with a radius of 0.6 kilometers
        final GeoQuery geoQuery;
        geoQuery = geoFire.
                queryAtLocation(new GeoLocation(mLatLang.latitude,
                        mLatLang.longitude), GIOQUERY_RADIUS);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                directory.tripin.com.tripindirectory.helper.Logger.v("OnKeyEntered: " + key);

                FoundHubPojo foundHubPojo = new FoundHubPojo(key,
                        getDistance(mLatLang.latitude,
                                mLatLang.longitude,
                                location.latitude,
                                location.longitude));
                mNearestHubsList.add(foundHubPojo);
            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
            }

            @Override
            public void onGeoQueryReady() {
                directory.tripin.com.tripindirectory.helper.Logger.v("ONGEOQUERYREADY");
                if (mNearestHubsList.size() == 0) {
                    mRadiusMultiplier = mRadiusMultiplier + 1;
                    directory.tripin.com.tripindirectory.helper.Logger.v("Radius Incrimented: " + mRadiusMultiplier);
                    geoQuery.setRadius(GIOQUERY_RADIUS * mRadiusMultiplier);
                } else {
                    Collections.sort(mNearestHubsList, new Comparator<FoundHubPojo>() {
                        @Override
                        public int compare(FoundHubPojo t1, FoundHubPojo t2) {
                            return t1.getmHubDistanceFromCenter().compareTo(t2.getmHubDistanceFromCenter());
                        }
                    });
                    mNearestHub = mNearestHubsList.get(0);
                    mNearestHubsList.clear();
                    if(hubFetchedCallback!=null){
                        switch (mCityType){
                            case 0:{
                                break;
                            }
                            case 1:{
                                hubFetchedCallback.onSourceHubFetched(mNearestHub.getmHubName(),mOperationTag);
                                break;
                            }
                            case 2:{
                                hubFetchedCallback.onDestinationHubFetched(mNearestHub.getmHubName(),mOperationTag);
                                break;
                            }

                        }
                    }
                    directory.tripin.com.tripindirectory.helper.Logger.v("Hub: " + mNearestHub.getmHubName());
                    isFetchingHub = false;
                    geoQuery.removeAllListeners();

                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }

    public FoundHubPojo getmNearestHub() {
        return mNearestHub;
    }

    public void setmNearestHub(FoundHubPojo mNearestHub) {
        this.mNearestHub = mNearestHub;
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address != null && address.size() > 0) {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
                return p1;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return p1;
        }
        return p1;
    }

    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}