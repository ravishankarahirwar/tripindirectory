package directory.tripin.com.tripindirectory.newlookcode.pojos;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class InteractionPojo {

    private String mUID;
    private String mFUID;
    private String mRMN;
    private String mCompanyName;
    private String mDisplayName;
    private String mFCM;
    private String mOUID;
    private String mOFUID;
    private String mORMN;
    private String mOcompanyName;
    private String mOdisplayName;
    private String mOFCM;

    @ServerTimestamp
    Date mTimeStamp;

    public InteractionPojo(String mUID, String mFUID, String mRMN, String mCompanyName, String mDisplayName, String mFCM, String mOUID, String mOFUID, String mORMN, String mOcompanyName, String mOdisplayName, String mOFCM) {
        this.mUID = mUID;
        this.mFUID = mFUID;
        this.mRMN = mRMN;
        this.mCompanyName = mCompanyName;
        this.mDisplayName = mDisplayName;
        this.mFCM = mFCM;
        this.mOUID = mOUID;
        this.mOFUID = mOFUID;
        this.mORMN = mORMN;
        this.mOcompanyName = mOcompanyName;
        this.mOdisplayName = mOdisplayName;
        this.mOFCM = mOFCM;
    }

    public InteractionPojo() {
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

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
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

    public String getmFCM() {
        return mFCM;
    }

    public void setmFCM(String mFCM) {
        this.mFCM = mFCM;
    }

    public String getmOUID() {
        return mOUID;
    }

    public void setmOUID(String mOUID) {
        this.mOUID = mOUID;
    }

    public String getmOFUID() {
        return mOFUID;
    }

    public void setmOFUID(String mOFUID) {
        this.mOFUID = mOFUID;
    }

    public String getmORMN() {
        return mORMN;
    }

    public void setmORMN(String mORMN) {
        this.mORMN = mORMN;
    }

    public String getmOcompanyName() {
        return mOcompanyName;
    }

    public void setmOcompanyName(String mOcompanyName) {
        this.mOcompanyName = mOcompanyName;
    }

    public String getmOdisplayName() {
        return mOdisplayName;
    }

    public void setmOdisplayName(String mOdisplayName) {
        this.mOdisplayName = mOdisplayName;
    }

    public String getmOFCM() {
        return mOFCM;
    }

    public void setmOFCM(String mOFCM) {
        this.mOFCM = mOFCM;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
