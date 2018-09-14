package directory.tripin.com.tripindirectory.newprofiles.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ConnectPojo {

    private String mUid;
    private String mRmn;
    private String mFuid;
    private Boolean mStatus;

    @ServerTimestamp
    private Date mTimeStamp;

    private String mDisplayName;
    private String mPhotoUrl;
    private String mCompanyName;

    public ConnectPojo(String mUid, Boolean mStatus, String mDisplayName, String mPhotoUrl, String mRmn, String mFuid, String mCompanyName) {
        this.mUid = mUid;
        this.mStatus = mStatus;
        this.mDisplayName = mDisplayName;
        this.mPhotoUrl = mPhotoUrl;
        this.mRmn = mRmn;
        this.mFuid = mFuid;
        this.mCompanyName = mCompanyName;
    }

    public ConnectPojo() {
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmFuid() {
        return mFuid;
    }

    public void setmFuid(String mFuid) {
        this.mFuid = mFuid;
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

    public Boolean getmStatus() {
        return mStatus;
    }

    public void setmStatus(Boolean mStatus) {
        this.mStatus = mStatus;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public void setmPhotoUrl(String mPhotoUrl) {
        this.mPhotoUrl = mPhotoUrl;
    }
}
