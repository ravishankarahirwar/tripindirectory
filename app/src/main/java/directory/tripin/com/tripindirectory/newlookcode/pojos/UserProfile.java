package directory.tripin.com.tripindirectory.newlookcode.pojos;

public class UserProfile {

    private String mDisplayName;
    private String mRMN;
    private String mEmail;
    private String mImageUrl;
    private String mUid;
    private String mFCM;

    public UserProfile(String mDisplayName, String mRMN, String mEmail, String mImageUrl, String mUid, String mFCM) {
        this.mDisplayName = mDisplayName;
        this.mRMN = mRMN;
        this.mEmail = mEmail;
        this.mImageUrl = mImageUrl;
        this.mUid = mUid;
        this.mFCM = mFCM;
    }

    public UserProfile() {
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

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmFCM() {
        return mFCM;
    }

    public void setmFCM(String mFCM) {
        this.mFCM = mFCM;
    }
}
