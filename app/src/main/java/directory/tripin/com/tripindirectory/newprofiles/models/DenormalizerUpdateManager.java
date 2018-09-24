package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

import directory.tripin.com.tripindirectory.model.CompanyAddressPojo;

public class DenormalizerUpdateManager {

    DocumentReference reference;
    DenormalizerPojo denormalizerPojo;
    DenormUpdateListner denormUpdateListner;

    public DenormalizerUpdateManager(DocumentReference reference, DenormalizerPojo denormalizerPojo, DenormUpdateListner denormUpdateListner) {
        this.reference = reference;
        this.denormalizerPojo = denormalizerPojo;
        this.denormUpdateListner = denormUpdateListner;
        update();
    }

    private void update() {
        HashMap<String, DenormalizerPojo> hashMap3 = new HashMap<>();
        hashMap3.put("mDetails", denormalizerPojo);
        reference.set(hashMap3, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                denormUpdateListner.onUploaded();
            }
        });

    }


}
