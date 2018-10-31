package directory.tripin.com.tripindirectory.newprofiles.models;

public class RateReminderPojo {

    private String mCompanyName;
    private String mDisplayName;
    private String mRMN;
    private String mUID;
    private String mFUID;
    private String mAction;
    private String mTimeStamp;
    private Double mRatings;
    private Boolean mIsActive;

    public RateReminderPojo(String mCompanyName, String mDisplayName, String mRMN, String mUID, String mFUID, String mAction, String mTimeStamp, Double mRatings, Boolean mIsActive) {
        this.mCompanyName = mCompanyName;
        this.mDisplayName = mDisplayName;
        this.mRMN = mRMN;
        this.mUID = mUID;
        this.mFUID = mFUID;
        this.mAction = mAction;
        this.mTimeStamp = mTimeStamp;
        this.mRatings = mRatings;
        this.mIsActive = mIsActive;
    }

    public RateReminderPojo() {
    }

    public Boolean getmIsActive() {
        return mIsActive;
    }

    public void setmIsActive(Boolean mIsActive) {
        this.mIsActive = mIsActive;
    }

    public Double getmRatings() {
        return mRatings;
    }

    public void setmRatings(Double mRatings) {
        this.mRatings = mRatings;
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

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public String getmUID() {
        return mUID;
    }

    public void setmUID(String mUID) {
        this.mUID = mUID;
    }

    public String getmFUID() {
        return mFUID;
    }

    public void setmFUID(String mFUID) {
        this.mFUID = mFUID;
    }

    public String getmAction() {
        return mAction;
    }

    public void setmAction(String mAction) {
        this.mAction = mAction;
    }

    public String getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
