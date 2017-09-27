package directory.tripin.com.tripindirectory;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import directory.tripin.com.tripindirectory.adapters.PartnersAdapter;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.model.response.Contact;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;
import directory.tripin.com.tripindirectory.utils.SpaceTokenizer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    public static final int CONTACT_LOADER_ID = 3;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int FINE_LOCATION_PERMISSIONS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;
    private Context mContext;
    private PartnersManager mPartnerManager;
    private RecyclerView mPartnerList;
    private GetPartnersResponse mPartnerListResponse;
    private PartnersAdapter mPartnersAdapter;
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private ProgressDialog pd;
    private CursorLoader mCursorLoader;

    private ArrayList<Contact> mAllContact = new ArrayList<>();
    private ArrayList<Contact> mMatchedContacts = new ArrayList<>();

    private Map<String, String> mContactMap = new HashMap<String, String>();

    private boolean isFromLocationButton = false;

    private RelativeLayout mContentMainParent;

    private DrawerLayout mDrawer;

    /**
     * Search_Field
     */
    private MultiAutoCompleteTextView mSearchField;

    private ImageView mLocationBtn;
    private ImageView mVoiceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setContentInsetStartWithNavigation(0); //for reducing gap b/w Hamburger menu and MultiAutocompleteTextView
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mContentMainParent = (RelativeLayout) findViewById(R.id.content_main_parent_layout);

        mPartnerList = (RecyclerView) findViewById(R.id.partner_list);

        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mPartnerList.setLayoutManager(verticalLayoutManager);

        mContext = this;
        mPartnerManager = new PartnersManager(mContext);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initCustomSearch();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLocations().get(0);
//                for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                // ...
                mLastLocation = location;
                Logger.v("Locations :" + location.getLatitude() + "," + location.getLatitude());

                if (isFromLocationButton) {
                    getLocationName();
                } else {
                    if (locationResult != null) {
                        mMatchedContacts.clear();
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setMessage("loading");
                        pd.show();
                        fetchPartners("", String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        stopLocationUpdates();
                    }
                }
//                }
            }
        };

        getContactsPermission();
        getLocationsPermission();
    }

    /**
     * Search_Field
     */
    private void initCustomSearch() {
        mSearchField = (MultiAutoCompleteTextView) this.findViewById(R.id.search_field);
        mSearchField.setTokenizer(new SpaceTokenizer());

        mVoiceSearch = (ImageView) this.findViewById(R.id.voice_search);
        mLocationBtn = (ImageView) this.findViewById(R.id.location);
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    mAllContact.clear();
//                    mMatchedContacts.clear();
                    mMatchedContacts.clear();
                    pd = new ProgressDialog(MainActivity.this);
                    pd.setMessage("loading");
                    pd.show();
                    fetchPartners(mSearchField.getText().toString(), "null", "null");
                    return true;
                }
                return false;
            }
        });

        mSearchField.setThreshold(1);
        mSearchField.setAdapter(ArrayAdapter.createFromResource(MainActivity.this, R.array.planets_array,
                android.R.layout.simple_dropdown_item_1line));

        mVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromLocationButton = true;
                mMatchedContacts.clear();

                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("loading");
                pd.show();
                startVoiceRecognitionActivity();
            }
        });

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFromLocationButton = true;
                mMatchedContacts.clear();
                getLocationsPermission();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.location_button) {
            isFromLocationButton = true;
            mMatchedContacts.clear();
            getLocationsPermission();
        } else if (id == R.id.location_mic) {
            isFromLocationButton = true;
            mMatchedContacts.clear();
            startVoiceRecognitionActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchPartners(String enquiry, String lat, String lng) {

        mPartnerManager.getPartnersList(enquiry, "", "", "", "",
                "", "", lat, lng, "0", "20", new PartnersManager.GetPartnersListener() {
                    @Override
                    public void onSuccess(GetPartnersResponse getPartnersResponse) {
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        mPartnerListResponse = getPartnersResponse;
                        List<Contact> contacts = new ArrayList<Contact>();
                        Contact contact1 = new Contact("Nitesh K. Bagadia", "9820193701");
                        contacts.add(contact1);

                        checkForNumbersMatched();
                    }

                    @Override
                    public void onFailed() {
                    }
                });
    }

    private void getLocationsPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_PERMISSIONS);

                Logger.v("Coming in rationale");
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_PERMISSIONS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

                Logger.v("Coming in non rationale");
            }
        } else {
            createLocationRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Logger.v("Locations permission : true");

                    createLocationRequest();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.v("Locations permission : false");
                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Logger.v("Contacts permission : true");

                    getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), this);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Logger.v("Contacts permission : false");
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationSettings();
    }

    private void locationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                callFusedLocationApi();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                callFusedLocationApi();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
                Logger.v("permission for gps denied");
            }
        }

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            Toast.makeText(MainActivity.this, matches.get(0).toString(), Toast.LENGTH_SHORT).show();
            String enquiry = matches.get(0).toString();
//            mSearchBox.setText("");  //TODO
//            mSearchBox.setText(enquiry); //TODO
            mSearchField.setText("");
            mSearchField.setText(enquiry);
            fetchPartners(enquiry, "null", "null");

        }
    }

    private void callFusedLocationApi() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            getLocationsPermission();

        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    private void getLocationName() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);

//                mSearchBox.setText(address.getLocality());  //TODO
                mSearchField.setText(address.getLocality());
                pd = new ProgressDialog(MainActivity.this);
                pd.setMessage("loading");
                pd.show();
//                fetchPartners(mSearchBox.getText().toString(), "null", "null");//TODO
                fetchPartners(mSearchField.getText().toString(), "null", "null");
                stopLocationUpdates();
            }
        } catch (IOException e) {
            Logger.v("Unable to connect to Geocoder " + e);
        }
    }

    private void getContactsPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            Logger.v("Contacts permission provided");
            getSupportLoaderManager().initLoader(CONTACT_LOADER_ID, new Bundle(), this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mCursorLoader = new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if ((!data.isClosed()) && data.getCount() > 0) {
            while (data.moveToNext()) {
                String name = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                String phonenumber = data.getString(data.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phonenumber);
                mAllContact.add(contact);
                mContactMap.put(phonenumber, name);
            }
            data.close();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.reset();
    }

    private void checkForNumbersMatched() {
        new MatchContact().execute(null, null, null);
        Logger.v("No of matched contacts " + mMatchedContacts.size());
    }

    //-------------------- Voice -----------
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak Enquiry");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    class MatchContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            mPartnersAdapter = new PartnersAdapter(MainActivity.this, mPartnerListResponse, mMatchedContacts,
//                    mSearchBox.getText().toString(), mContentMainParent); //TODO
            mPartnersAdapter = new PartnersAdapter(MainActivity.this, mPartnerListResponse, mMatchedContacts,
                    mSearchField.getText().toString(), mContentMainParent);
            mPartnerList.setAdapter(mPartnersAdapter);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < mPartnerListResponse.getData().size(); i++) {
                for (Contact contact : mAllContact) {
                    String sContat = mPartnerListResponse.getData().get(i).getContact().getContact();

                    if (PhoneNumberUtils.compare(sContat, contact.getPhone())) {
                        mMatchedContacts.add(contact);
                        break;
                    }
                }
            }
            return null;
        }
    }
}
