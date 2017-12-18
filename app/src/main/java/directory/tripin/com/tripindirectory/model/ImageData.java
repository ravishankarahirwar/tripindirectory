package directory.tripin.com.tripindirectory.model;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Shubham on 12/7/2017.
 */

public class ImageData {

    private Uri mImageUri;
    private  String mImageUrl = "";
    private Bitmap mImageBitmap;
    private Boolean isSet = false;

    public ImageData(){

    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public ImageData(Uri mImageUri, Bitmap mImageBitmap) {
        this.mImageUri = mImageUri;
        this.mImageBitmap = mImageBitmap;
        isSet = true;
    }

    public Boolean getSet() {
        return isSet;
    }

    public void setSet(Boolean set) {
        isSet = set;
    }

    public Uri getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(Uri mImageUri) {
        this.mImageUri = mImageUri;
    }

    public Bitmap getmImageBitmap() {
        return mImageBitmap;
    }

    public void setmImageBitmap(Bitmap mImageBitmap) {
        this.mImageBitmap = mImageBitmap;
    }
}
