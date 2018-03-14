package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Shubham on 2/24/2018.
 */

public class QuotePojo {

    private String mQuoteAmount;
    private String mComment = "";

    @ServerTimestamp
    private Date mTimeStamp;

    private String mUid;
    private String mDocId;
    private String mRMN;
    private String mCompanyName = "";
    private String mImageUrl;

    private String mFcmToken;


    public QuotePojo(String mQuoteAmount, String mComment, String mUid, String mDocId, String mRMN, String mFcmToken) {
        this.mQuoteAmount = mQuoteAmount;
        this.mComment = mComment;
        this.mUid = mUid;
        this.mDocId = mDocId;
        this.mRMN = mRMN;
        this.mFcmToken = mFcmToken;
    }

    public QuotePojo() {
    }

    public String getmFcmToken() {
        return mFcmToken;
    }

    public void setmFcmToken(String mFcmToken) {
        this.mFcmToken = mFcmToken;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }


    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmCompanyName() {
        return mCompanyName;
    }

    public void setmCompanyName(String mCompanyName) {
        this.mCompanyName = mCompanyName;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmDocId() {
        return mDocId;
    }

    public void setmDocId(String mDocId) {
        this.mDocId = mDocId;
    }

    public String getmQuoteAmount() {
        return mQuoteAmount;
    }

    public void setmQuoteAmount(String mQuoteAmount) {
        this.mQuoteAmount = mQuoteAmount;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

//    public Date getmTimeStamp() {
//        return mTimeStamp;
//    }
//
//    public void setmTimeStamp(Date mTimeStamp) {
//        this.mTimeStamp = mTimeStamp;
//    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getInitiatorMsgText() {
        return ">>Quotation Amount : " + getmQuoteAmount() + " ₹\n" +
                ">>Comment : "+getmComment();
    }
}
