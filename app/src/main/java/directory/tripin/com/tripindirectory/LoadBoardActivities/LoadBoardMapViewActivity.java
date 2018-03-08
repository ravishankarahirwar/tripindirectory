package directory.tripin.com.tripindirectory.LoadBoardActivities;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.LoadBoardActivities.models.LoadPostPojo;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.interfaces.RouteFinderListner;
import directory.tripin.com.tripindirectory.manager.MapAnimator;
import directory.tripin.com.tripindirectory.manager.RouteManager;

public class LoadBoardMapViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MAP_PADDING = 50;
    private static final LatLngBounds BOUNDS_INDIA =
            new LatLngBounds(new LatLng(23.63936, 68.14712),
                    new LatLng(28.20453, 97.34466));


    GoogleMap map;
    MapView mapView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_board_map_view);
        mapView = findViewById(R.id.mapViewLoadboard);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chatheads, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notifications: {
                break;
            }
            case R.id.action_comments: {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Query query = FirebaseFirestore.getInstance()
                .collection("loads")
                .orderBy("mTimeStamp", Query.Direction.DESCENDING)
                .limit(10);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                List<LoadPostPojo> loadPostPojos = new ArrayList<>();

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(BOUNDS_INDIA, MAP_PADDING);
                map.animateCamera(cu);

                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                    LoadPostPojo loadPostPojo = documentSnapshot.toObject(LoadPostPojo.class);
                    drawRouteOnMap(new LatLng(loadPostPojo.getmSourceCityLatLang().getLatitude(),
                            loadPostPojo.getmSourceCityLatLang().getLongitude()), new LatLng(loadPostPojo.getmDestinationCityLatLang().getLatitude(),
                            loadPostPojo.getmDestinationCityLatLang().getLongitude()));
                }


            }
        });
    }

    private void drawRouteOnMap(final LatLng source, final LatLng destination) {
        if (isInternetAvailable(getApplicationContext())) {
            RouteManager routeManager = new RouteManager(source, destination, new RouteFinderListner() {
                @Override
                public void onRouteFind(ArrayList<LatLng> points) {
                    PolylineOptions lineOptions = new PolylineOptions();
                    lineOptions.addAll(points);
                    lineOptions.geodesic(true);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);
                    // Drawing polyline in the Google Map for the i-th route
                    if (lineOptions != null) {


                        startAnim(points);
                    } else {
                        Logger.v("onPostExecute : without Polylines drawn");
                    }
                }

                @Override
                public void onNoRouteFind() {
                    Toast.makeText(getApplicationContext(), "No route found", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDistanceFind(String distance) {

                }

                @Override
                public void onEstimatedTimeFind(String time) {

                }
            });
        }
    }

    private void startAnim(ArrayList<LatLng> points) {
        if (map != null) {
            if (points != null && points.size() > 1) {
                MapAnimator mapAnimator = new MapAnimator();
                mapAnimator.animateRoute(
                        map, points);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        return connected;
    }


}
