package directory.tripin.com.tripindirectory.FormActivities.FormFragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import directory.tripin.com.tripindirectory.FormActivities.CompanyInfoActivity;
import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.activity.AddCompanyActivity;
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
public class ImagesFormFragment extends BaseFragment implements AddImage,EasyImagePickUP.ImagePickerListener {


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



    public ImagesFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrlList = new ArrayList<>();

        //initially add 3 blank ImageData Objects
        images = new ArrayList<>();
        images.add(new ImageData());
        images.add(new ImageData());
        images.add(new ImageData());

        imagesRecyclarAdapter = new ImagesRecyclarAdapter(images,this,getActivity());
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
        recyclerView = view.findViewById(R.id.imageslist);
        recyclerView.hasFixedSize();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        easyImagePickUP = new EasyImagePickUP(getActivity().getParent());
        imagesUriList = new ArrayList<>();
        progressDialog = new ProgressDialog(getActivity());
        fetchImagesURL();
        return view;
    }

    public void submit(View view) {

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



        List<String> urllist = new ArrayList<>();
        urllist.add("url1");
        urllist.add("url2");
        urllist.add("url3");

        Map<String, Boolean> source = new HashMap<>();
        Map<String, Boolean> destination = new HashMap<>();

        source.put("mumbai", true);
        source.put("nagpur", true);
        destination.put("rajkot", true);
        destination.put("gandhinagar", true);

        List<String> urls = new ArrayList<>();
        for(ImageData imageData: images){
            urls.add(imageData.getmImageUrl());
        }

        //db.collection("partners").document(mAuth.getUid()).set(partnerInfoPojo);
        Toast.makeText(getActivity(), "Data uploaded!", Toast.LENGTH_LONG).show();
    }


    private void fetchImagesURL() {

        db.collection("partners").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        PartnerInfoPojo partnerInfoPojo = task.getResult().toObject(PartnerInfoPojo.class);
                        mUrlList = partnerInfoPojo.getImagesUrl();
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
        easyImagePickUP.imagepicker(1);
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
