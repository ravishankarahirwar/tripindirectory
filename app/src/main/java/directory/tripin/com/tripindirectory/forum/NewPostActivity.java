package directory.tripin.com.tripindirectory.forum;

import android.graphics.Color;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.forum.models.Post;
import directory.tripin.com.tripindirectory.forum.models.User;


public class NewPostActivity extends BaseActivity {
    private static final int POST_LOAD = 1;
    private static final int POST_TRUCK = 2;
    private int POST_TYPE = POST_LOAD;
    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private FloatingActionButton mSubmitButton;
    private Toolbar toolbar;

    private Spinner vehicleType;
    private Spinner bodyType;

    private TextInputEditText mPayload;
    private TextInputEditText mLength;
    private TextInputEditText mSource;
    private TextInputEditText mDestination;
    private TextInputEditText mMaterial;

    private TextInputLayout mMaterialInputLayout;

    private RadioGroup mPostTypeGroup;
    private RadioButton mPostLoad;
    private RadioButton mPostTruck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        setupToolbar();

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]


        mSource = findViewById(R.id.source);
        mDestination = findViewById(R.id.destination);
        mMaterial = findViewById(R.id.material);
        vehicleType = findViewById(R.id.vehicle_type);
        bodyType = findViewById(R.id.body_type);
        mPayload = findViewById(R.id.input_payload);
        mLength = findViewById(R.id.input_length);

        mMaterialInputLayout = findViewById(R.id.input_layout_material);

        mPostTypeGroup = findViewById(R.id.post_type_group);
        mPostLoad = findViewById(R.id.post_load);
        mPostTruck = findViewById(R.id.post_truck);

        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost(view);
            }
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
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Now Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void submitPost(View view) {
        final String source = mSource.getText().toString();
        final String destination = mDestination.getText().toString();
        final String material = mMaterial.getText().toString();

        final String turckType = vehicleType.getSelectedItem().toString();
        final String bodyTypestr = bodyType.getSelectedItem().toString();
        final String payload = mPayload.getText().toString();
        final String length = mLength.getText().toString();

       final Post post = new Post(getUid(), "Ravi", POST_TYPE, source,   destination, material,"date", turckType,  bodyTypestr, length, payload,"Remark");

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
}
