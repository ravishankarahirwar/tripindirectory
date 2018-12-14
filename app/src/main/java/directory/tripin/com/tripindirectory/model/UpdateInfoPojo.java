package directory.tripin.com.tripindirectory.model;
/**
 * Created by Shubham on 1/10/2018.
 */

public class UpdateInfoPojo {

    private String mTimeStamp;
    private String mTitle = "";
    private String mDiscription = "";
    private String mType = "" ;
    private String mUrl = "";
    private String mImageUrl = "";

    public UpdateInfoPojo() {
    }

    public UpdateInfoPojo(String mTimeStamp, String mTitle, String mDiscription, String mType, String mUrl, String mImageUrl) {
        this.mTimeStamp = mTimeStamp;
        this.mTitle = mTitle;
        this.mDiscription = mDiscription;
        this.mType = mType;
        this.mUrl = mUrl;
        this.mImageUrl = mImageUrl;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDiscription() {
        return mDiscription;
    }

    public void setmDiscription(String mDiscription) {
        this.mDiscription = mDiscription;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}