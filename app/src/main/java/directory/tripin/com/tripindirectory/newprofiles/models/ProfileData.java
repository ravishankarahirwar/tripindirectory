package directory.tripin.com.tripindirectory.newprofiles.models;

import java.util.List;

public class ProfileData{
    private String mCompanyName;
    private String mDisplayName;
    private String mAddressCity;
    private String mImageUrl;
    private List<String> mFleets;
    private String mRmn;
    private String mUid;
    private String mFuid;

    public ProfileData(String mCompanyName, String mDisplayName, String mAddressCity, String mImageUrl, List<String> mFleets, String mRmn, String mUid, String mFuid) {
        this.mCompanyName = mCompanyName;
        this.mDisplayName = mDisplayName;
        this.mAddressCity = mAddressCity;
        this.mImageUrl = mImageUrl;
        this.mFleets = mFleets;
        this.mRmn = mRmn;
        this.mUid = mUid;
        this.mFuid = mFuid;
    }

    public ProfileData() {
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmAddressCity() {
        return mAddressCity;
    }

    public void setmAddressCity(String mAddressCity) {
        this.mAddressCity = mAddressCity;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public List<String> getmFleets() {
        return mFleets;
    }

    public void setmFleets(List<String> mFleets) {
        this.mFleets = mFleets;
    }

    public String getmRmn() {
        return mRmn;
    }

    public void setmRmn(String mRmn) {
        this.mRmn = mRmn;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmFuid() {
        return mFuid;
    }

    public void setmFuid(String mFuid) {
        this.mFuid = mFuid;
    }
}
