package directory.tripin.com.tripindirectory.newlookcode.pojos;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SuperRequestPojo {

    private String mUid;
    private String mFuid;
    private String mRmn;
    private Long mProfileType;
    private String mCompName;
    private String mUserName;
    private Long mRequestStatus;
    private Long mSelectedPlan;

    @ServerTimestamp
    Date mTimeStamp;

    public SuperRequestPojo(String mUid, String mFuid, String mRmn, Long mProfileType, String mCompName, String mUserName, Long mRequestStatus, Long mSelectedPlan ) {
        this.mUid = mUid;
        this.mFuid = mFuid;
        this.mRmn = mRmn;
        this.mProfileType = mProfileType;
        this.mCompName = mCompName;
        this.mUserName = mUserName;
        this.mRequestStatus = mRequestStatus;
        this.mSelectedPlan = mSelectedPlan;
    }

    public SuperRequestPojo() {
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

    public String getmRmn() {
        return mRmn;
    }

    public void setmRmn(String mRmn) {
        this.mRmn = mRmn;
    }

    public Long getmProfileType() {
        return mProfileType;
    }

    public void setmProfileType(Long mProfileType) {
        this.mProfileType = mProfileType;
    }

    public String getmCompName() {
        return mCompName;
    }

    public void setmCompName(String mCompName) {
        this.mCompName = mCompName;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public Long getmRequestStatus() {
        return mRequestStatus;
    }

    public void setmRequestStatus(Long mRequestStatus) {
        this.mRequestStatus = mRequestStatus;
    }

    public Long getmSelectedPlan() {
        return mSelectedPlan;
    }

    public void setmSelectedPlan(Long mSelectedPlan) {
        this.mSelectedPlan = mSelectedPlan;
    }
}
