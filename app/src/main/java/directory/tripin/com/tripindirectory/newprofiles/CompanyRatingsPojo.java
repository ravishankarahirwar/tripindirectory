package directory.tripin.com.tripindirectory.newprofiles;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;


public class CompanyRatingsPojo {

    private String mUserName;
    private String mImageUrl;
    private String mRMN;
    private Double mRitings;
    private String mReview;
    private String mUid;
    private String mReciversFCMToken;

    @ServerTimestamp
    private Date serverTimestamp;

    public CompanyRatingsPojo(String mUserName, String mImageUrl, String mRMN, String mUid, Double mRitings, String mReview, String mReciversFCMToken) {
        this.mUserName = mUserName;
        this.mImageUrl = mImageUrl;
        this.mRMN = mRMN;
        this.mRitings = mRitings;
        this.mReview = mReview;
        this.mUid = mUid;
        this.mReciversFCMToken = mReciversFCMToken;
    }

    public CompanyRatingsPojo() {
    }

    public String getmReciversFCMToken() {
        return mReciversFCMToken;
    }

    public void setmReciversFCMToken(String mReciversFCMToken) {
        this.mReciversFCMToken = mReciversFCMToken;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public Double getmRitings() {
        return mRitings;
    }

    public void setmRitings(Double mRitings) {
        this.mRitings = mRitings;
    }

    public String getmReview() {
        return mReview;
    }

    public void setmReview(String mReview) {
        this.mReview = mReview;
    }

    public Date getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Date serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
