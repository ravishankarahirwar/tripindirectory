package directory.tripin.com.tripindirectory.forum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.forum.models.Post;
import directory.tripin.com.tripindirectory.forum.models.User;
import directory.tripin.com.tripindirectory.helper.Logger;


public class NewPostActivity extends BaseActivity {
    private static final int POST_LOAD = 1;
    private static final int POST_TRUCK = 2;
    private int POST_TYPE = POST_LOAD;
    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]
    private TextView mPaste;
    private EditText mTitleField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;
    private Toolbar toolbar;

    private Spinner vehicleType;
    private Spinner bodyType;

    private TextInputEditText mPayload;
    private TextInputEditText mLength;
    private EditText mSource;
    private EditText mDestination;
    private TextInputEditText mMaterial;
    private TextInputEditText mPostRequirement;


    private TextInputLayout mMaterialInputLayout;

    private RadioGroup mPostTypeGroup;
    private RadioButton mPostLoad;
    private RadioButton mPostTruck;

    private int SELECT_SOURCE = 1;
    private int SELECT_DESTINATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        setupToolbar();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSource = findViewById(R.id.source);
        mDestination = findViewById(R.id.destination);
        mMaterial = findViewById(R.id.material);
        vehicleType = findViewById(R.id.vehicle_type);
        bodyType = findViewById(R.id.body_type);
        mPayload = findViewById(R.id.input_payload);
        mLength = findViewById(R.id.input_length);
        mPostRequirement = findViewById(R.id.post_requirement);
        mPaste = findViewById(R.id.paste);
        mMaterialInputLayout = findViewById(R.id.input_layout_material);

        mPostTypeGroup = findViewById(R.id.post_type_group);
        mPostLoad = findViewById(R.id.post_load);
        mPostTruck = findViewById(R.id.post_truck);

        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String postRequirement = mPostRequirement.getText().toString();
                final String source = mSource.getText().toString();

                if( (postRequirement != null || source != null) && (postRequirement.trim().length() > 0 || source.trim().length() > 0)) {
                    submitPost();
                } else {
                    Toast.makeText(NewPostActivity.this,"Please fill Source or Requirement", Toast.LENGTH_SHORT).show();
                }  }
        });

        ArrayAdapter<CharSequence> truckType = ArrayAdapter.createFromResource(this,
                R.array.truck_type, R.layout.spinner_item);

        ArrayAdapter<CharSequence> bodyTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.body_type, android.R.layout.simple_spinner_item);

        vehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
                vehicleType.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        bodyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.BLACK);
                bodyType.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        truckType.setDropDownViewResource(R.layout.spinner_item);
        bodyTypeAdapter.setDropDownViewResource(R.layout.spinner_item);

        vehicleType.setAdapter(truckType);
        bodyType.setAdapter(bodyTypeAdapter);

        viewSetup();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_SOURCE && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(NewPostActivity.this, data);
                String sourceCityName = place.getName().toString().toUpperCase();
                mSource.setText(sourceCityName);
                }
            } else if (requestCode == SELECT_DESTINATION && resultCode == RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(NewPostActivity.this, data);
            String sourceCityName = place.getName().toString().toUpperCase();
            mDestination.setText(sourceCityName);
        }
        }
    private void viewSetup() {
        mPostTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int radioButtonID) {
                if (radioButtonID == R.id.post_load) {
                    POST_TYPE = POST_LOAD;
                    mMaterialInputLayout.setVisibility(TextInputLayout.VISIBLE);
                } else if (radioButtonID == R.id.post_truck) {
                    mMaterialInputLayout.setVisibility(TextInputLayout.GONE);
                    POST_TYPE = POST_TRUCK;
                }
            }
        });

        mSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttheplacesfragment(SELECT_SOURCE);
            }
        });

        mDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttheplacesfragment(SELECT_DESTINATION);
            }
        });

        mPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pasteString = paste();
                if(pasteString != null && (!pasteString.isEmpty()) && pasteString.length() > 0) {
                    mPostRequirement.setText(pasteString);
                } else {
                   Toast.makeText(NewPostActivity.this, "Nothing to paste, please copy first",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String paste() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getText() != null) {
                return clipboard.getText().toString();
            } else {
                return null;
            }
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null &&  clipboard.getPrimaryClip() != null && clipboard.getPrimaryClip().getItemCount() > 0) {
                android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                if (item.getText() != null) {
                    return clipboard.getText().toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Now Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void starttheplacesfragment(int selectFor){
        try {

            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .setCountry("IN")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(NewPostActivity.this);
            startActivityForResult(intent, selectFor);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
    private void submitPost() {
        final String source = mSource.getText().toString().toUpperCase().trim();
        final String destination = mDestination.getText().toString().toUpperCase().trim();
        final String material = mMaterial.getText().toString().toUpperCase().trim();

        final String turckType = vehicleType.getSelectedItem().toString().trim();
        final String bodyTypestr = bodyType.getSelectedItem().toString().trim();
        final String payload = mPayload.getText().toString().trim();
        final String length = mLength.getText().toString().trim();
        final String postinhDate = getDate();
        final String postRequirement = mPostRequirement.getText().toString();
        final String userPhoneNo = getUserPhoneNo();

        final Post post = new Post(getUid(), userPhoneNo, POST_TYPE, source,   destination, material, postinhDate, turckType,  bodyTypestr, length, payload, postRequirement, userPhoneNo);

            // Title is required
        //if (TextUtils.isEmpty(title)) {
            //mTitleField.setError(REQUIRED);
         //   return;
       // }

//        // Body is required
//        if (TextUtils.isEmpty(body)) {
//            mBodyField.setError(REQUIRED);
//            return;
//        }

            // Disable button so there are no multi-posts
//            setEditingEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

            // [START single_value_read]
            final String userId = getUid();
            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            User user = dataSnapshot.getValue(User.class);

                            // [START_EXCLUDE]
                            if (user == null) {
                                // User is null, error out
                                Log.e(TAG, "User " + userId + " is unexpectedly null");
                                Toast.makeText(NewPostActivity.this,
                                        "Error: could not fetch user.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                // Write new post
                                writeNewPost(userId, post);
                            }

                            // Finish this Activity, back to the stream
//                            setEditingEnabled(true);
                            finish();
                            // [END_EXCLUDE]
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            // [START_EXCLUDE]
//                            setEditingEnabled(true);
                            // [END_EXCLUDE]
                        }
                    });
            // [END single_value_read]

    }

//    private void setEditingEnabled(boolean enabled) {
//        mTitleField.setEnabled(enabled);
//        mBodyField.setEnabled(enabled);
//        if (enabled) {
//            mSubmitButton.setVisibility(View.VISIBLE);
//        } else {
//            mSubmitButton.setVisibility(View.GONE);
//        }
//    }

    // [START write_fan_out]
    private void writeNewPost(String userId,Post post) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
//        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]

    private String getDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("MMM dd, HH:mm");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
