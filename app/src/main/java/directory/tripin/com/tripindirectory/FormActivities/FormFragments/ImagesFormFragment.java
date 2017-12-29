package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.ImagesRecyclarAdapter;
import directory.tripin.com.tripindirectory.model.AddImage;
import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;
import directory.tripin.com.tripindirectory.model.ImageData;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;
import directory.tripin.com.tripindirectory.utils.EasyImagePickUP;
import directory.tripin.com.tripindirectory.viewmodel.AddPerson;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImagesFormFragment extends BaseFragment implements AddImage, EasyImagePickUP.ImagePickerListener {


    boolean canSubmit = true;
    boolean areImagesUploaded = false;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    List<ImageData> images;
    List<Uri> imagesUriList;
    EasyImagePickUP easyImagePickUP;
    ImagesRecyclarAdapter imagesRecyclarAdapter;
    int position;
    private StorageReference mStorageRef;
    StorageReference imagesRef;
    ProgressDialog progressDialog;
    List<String> mUrlList;
    private FirebaseFirestore db;
    Context mContext;
    private Button mImageUpload;
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagesRecyclarAdapter = new ImagesRecyclarAdapter(images,this,getActivity());

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
        mImageUpload = (Button)view.findViewById(R.id.image_upload);
        recyclerView = view.findViewById(R.id.imageslist);
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        easyImagePickUP = new EasyImagePickUP(getActivity(), this);
        imagesUriList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());

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

        if(images.get(index).getSet()){
            Uri file = images.get(index).getmImageUri();
            if (file != null) {
                if (mAuth.getCurrentUser() != null) {
                    imagesRef = mStorageRef.child(mAuth.getUid()).child(index + ".jpeg");
                    progressDialog.setTitle("uploading image " + index);
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
                                    Log.e("onSuccessUpload..", exception.toString());

                                    // Handle unsuccessful uploads
                                    // ...
                                }
                            });
                }
            }
        }else {
            if (index < 2) {
                uploadImagesandGetURL(index + 1);
            }else {
                Toast.makeText(getActivity(), "no images to upload", Toast.LENGTH_SHORT).show();
                uploadData();
            }

        }
        Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_LONG).show();
    }

    private void uploadData() {

        List<String> urls = new ArrayList<>();
        for(ImageData imageData: images){
            urls.add(imageData.getmImageUrl());
        }

        Map<String, List<String>> data = new HashMap<>();
        data.put("mImagesUrl", urls);
        mUserDocRef.set(data, SetOptions.merge());

//        db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(getActivity(), "Data uploaded!", Toast.LENGTH_LONG).show();
    }


    private void fetchImagesURL() {

        mUserDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        PartnerInfoPojo partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                        mUrlList = partnerInfoPojo.getmImagesUrl();
                        if(mUrlList!=null){
                            for(int i=0;i<mUrlList.size();i++){
                                images.get(i).setmImageUrl(mUrlList.get(i));
                            }
                            imagesRecyclarAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });


        recyclerView.setAdapter(imagesRecyclarAdapter);
    }

    @Override
    public void onPickClicked(int position) {
        easyImagePickUP.imagepicker(position);
        Log.e("tagg", "onPickClicked");
        this.position = position;

    }

    @Override
    public void onCancelClicked(int position) {
        Log.e("tagg", "onCancel");

        images.set(position, new ImageData());
        imagesRecyclarAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCamera() {
        Uri imageUri;
        ContentValues values = new ContentValues();
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent1.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
