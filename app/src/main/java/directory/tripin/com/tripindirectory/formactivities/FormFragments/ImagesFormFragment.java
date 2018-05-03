package directory.tripin.com.tripindirectory.formactivities.FormFragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.ImagesRecyclarAdapter;
import directory.tripin.com.tripindirectory.model.AddImage;
import directory.tripin.com.tripindirectory.model.ImageData;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.DB;
import directory.tripin.com.tripindirectory.utils.EasyImagePickUP;
import directory.tripin.com.tripindirectory.utils.TextUtils;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFormFragment extends BaseFragment implements AddImage, EasyImagePickUP.ImagePickerListener {

    private static final int OFFICE_IMAGE = 0;
    private static final int CARD_IMAGE = 1;
    private static final int SELF_IMAGE = 2;

    boolean canSubmit = true;
    boolean areImagesUploaded = false;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    List<ImageData> images;
    List<Uri> imagesUriList;
    EasyImagePickUP easyImagePickUP;
    Dialog dialog;
    ImagesRecyclarAdapter imagesRecyclarAdapter;
    int position;
    private StorageReference mStorageRef;
    StorageReference imagesRef;
    ProgressDialog progressDialog;
    List<String> mUrlList;
    private FirebaseFirestore db;
    Context mContext;
    private Button mImageUpload;
    TextView mCompName;
    TextView mCompAddress;
    TextUtils textUtils;
    TextView mSendReq;
    View view1;
    View view2;
    View view3;


    public ImagesFormFragment() {
        // Required empty public constructor
        //initially add 3 blank ImageData Objects
        images = new ArrayList<>();
        images.add(new ImageData());
        images.add(new ImageData());
        images.add(new ImageData());
        mUrlList = new ArrayList<>();

    }

    private DocumentReference mUserDocRef;
    private FirebaseAuth auth;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        textUtils = new TextUtils();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagesRecyclarAdapter = new ImagesRecyclarAdapter(images, this, getActivity());

        auth = FirebaseAuth.getInstance();
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(auth.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images_form, container, false);
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        mImageUpload = view.findViewById(R.id.image_upload);
        recyclerView = view.findViewById(R.id.imageslist);
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        easyImagePickUP = new EasyImagePickUP(getActivity(), this);
        imagesUriList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());

        mCompName = view.findViewById(R.id.company_name1);
        mCompAddress = view.findViewById(R.id.company_address);
        mSendReq = view.findViewById(R.id.textViewSendRequest);


        view1 = view.findViewById(R.id.acc_status_0);
        view2 = view.findViewById(R.id.acc_status_1);
        view3 = view.findViewById(R.id.acc_status_2);


        mImageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchImagesURL();
    }

    public void submit() {
        uploadImagesandGetURL(0);
    }

    void uploadImagesandGetURL(final int index) {

        if (images.get(index).getSet()) {
            Uri file = images.get(index).getmImageUri();
            if (file != null) {
                if (mAuth.getCurrentUser() != null) {
                    imagesRef = mStorageRef.child(mAuth.getUid()).child(index + ".jpeg");
                    progressDialog.setTitle("Uploading " + getImageName(index) + " Image...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    imagesRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get a URL to the uploaded content
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    images.get(index).setmImageUrl(downloadUrl.toString());
                                    progressDialog.hide();
                                    if (index + 1 <= 2) {
                                        uploadImagesandGetURL(index + 1);
                                    } else {
                                        uploadData();
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("onFailureUpload..", exception.toString());

                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            });
                }
            }
        } else {
            if (index < 2) {
                uploadImagesandGetURL(index + 1);
            } else {
                uploadData();
            }

        }
        mImageUpload.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.arrow_grey));
    }

    private String getImageName(int index) {
        String imageTitle = "";

        switch (index) {
            case OFFICE_IMAGE :
                imageTitle = "Office";
                break;
            case CARD_IMAGE :
                imageTitle = "Card";
                break;
            case SELF_IMAGE :
                imageTitle = "Self";
                break;
        }
        return imageTitle;
    }

    private void uploadData() {

        List<String> urls = new ArrayList<>();
        for (ImageData imageData : images) {
            urls.add(imageData.getmImageUrl());
        }

        Map<String, List<String>> data = new HashMap<>();
        data.put("mImagesUrl", urls);
        mUserDocRef.set(data, SetOptions.merge());

//        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(getActivity(), "Data Modified!", Toast.LENGTH_SHORT).show();
        mImageUpload.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.arrow_grey));
    }


    private void fetchImagesURL() {

        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);

        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        PartnerInfoPojo partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                        mUrlList = partnerInfoPojo.getmImagesUrl();

                        if (mUrlList != null) {
                            for (int i = 0; i < mUrlList.size(); i++) {
                                images.get(i).setmImageUrl(mUrlList.get(i));
                            }
                            imagesRecyclarAdapter.notifyDataSetChanged();
                        }


                        //manage view
                        switch (partnerInfoPojo.getmAccountStatus()) {
                            case 0: {
                                mCompName.setText(textUtils.toTitleCase(partnerInfoPojo.getmCompanyName()));
                                //set address
                                String addresstoset
                                        = partnerInfoPojo.getmCompanyAdderss().getAddress()
                                        + ", " + textUtils.toTitleCase(partnerInfoPojo.getmCompanyAdderss().getCity())
                                        + ", " + textUtils.toTitleCase(partnerInfoPojo.getmCompanyAdderss().getState());
                                if (partnerInfoPojo.getmCompanyAdderss().getPincode() != null) {
                                    addresstoset = addresstoset + ", " + partnerInfoPojo.getmCompanyAdderss().getPincode();
                                }
                                mCompAddress.setText(addresstoset);
                                mCompName
                                        .setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                        .getDrawable(getApplicationContext(),
                                                                R.drawable.ic_fiber_smart_record_bllue_24dp),
                                                null,
                                                null,
                                                null);

                                mSendReq.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                        alertDialog.setTitle(R.string.title_send_verification_request);
                                        alertDialog.setMessage(getString(R.string.alert_sent_verfication_req));
                                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.send_now),
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        mSendReq.setText("Sending...");
                                                        final HashMap<String, Object> hashMap6 = new HashMap<>();
                                                        hashMap6.put(DB.PartnerFields.ACCOUNT_STATUS, 1);
                                                        hashMap6.put(DB.PartnerFields.ACSUBMIT_FOR_APPROVAL, System.currentTimeMillis());

                                                        mUserDocRef.set(hashMap6, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //make layout 1 invisible make layout 2 visible
                                                                view1.setVisibility(View.GONE);
                                                                view2.setVisibility(View.VISIBLE);

                                                            }
                                                        });
                                                    }
                                                });
                                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL!",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        alertDialog.show();

                                    }
                                });
                                view1.setVisibility(View.VISIBLE);

                                break;
                            }
                            case 1: {
                                view2.setVisibility(View.VISIBLE);
                                break;
                            }
                            case 2: {
                                view3.setVisibility(View.VISIBLE);
                                break;
                            }
                        }

                    }
                }
            }
        });


        recyclerView.setAdapter(imagesRecyclarAdapter);
    }

    @Override
    public void onPickClicked(int position) {


        if (images.get(position).getmImageBitmap() != null) {
            createDialog(images.get(position), position);
        } else if (!images.get(position).getmImageUrl().isEmpty()) {
            createDialog(images.get(position), position);
        } else {
            easyImagePickUP.imagepicker(position);
            Log.e("tagg", "onPickClicked");
            this.position = position;
        }


    }

    private void createDialog(ImageData imageData, int p) {
        dialog = new Dialog(getContext());

        //SET TITLE
        switch (p) {
            case 0:
                dialog.setTitle("Office Image");
                break;
            case 1:
                dialog.setTitle("Card Image");
                break;
            case 2:
                dialog.setTitle("Self Image");
                break;
        }

        //set content
        dialog.setContentView(R.layout.dialog_image_view);


        ImageView imageView = dialog.findViewById(R.id.imageView);
        final LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);

        if (imageData.getmImageBitmap() != null) {
            dialog.show();
            imageView.setImageBitmap(imageData.getmImageBitmap());
            lottieAnimationView.setVisibility(View.INVISIBLE);

        } else if (!imageData.getmImageUrl().isEmpty()) {
            dialog.show();
            Picasso.with(getContext()).load(imageData.getmImageUrl()).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {

                }
            });
        }


    }

    @Override
    public void onCancelClicked(int position) {
        Log.e("tagg", "onCancel");

        images.set(position, new ImageData());
        imagesRecyclarAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCamera(Uri imageUri) {
//        Uri imageUri;
        ContentValues values = new ContentValues();
//        imageUri = getActivity().getContentResolver().insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent1, 0);
    }

    @Override
    public void onGallery() {
        Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent2.setType("image/*");
        startActivityForResult(intent2, 1);
    }

    @Override
    public void onPicked(int i, String s, Bitmap bitmap, Uri uri) {
        Log.e("tagg", "onPicked" + s);
        images.set(position, new ImageData(uri, bitmap));
        imagesUriList.add(uri);
        imagesRecyclarAdapter.notifyDataSetChanged();
        mImageUpload.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primaryColor));
    }

    @Override
    public void onCropped(int i, String s, Bitmap bitmap, Uri uri) {
        Log.e("tagg", "onPicked" + s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImagePickUP.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        easyImagePickUP.request_permission_result(requestCode, permissions, grantResults);
    }


    @Override
    public void onUpdate(PartnerInfoPojo partnerInfoPojo) {

    }


}
