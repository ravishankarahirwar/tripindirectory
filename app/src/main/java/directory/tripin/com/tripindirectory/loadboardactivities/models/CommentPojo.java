package directory.tripin.com.tripindirectory.loadboardactivities.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

/**
 * Created by Shubham on 2/22/2018.
 */

public class CommentPojo {

    private String mCommentText = "";
    private String mCompanyName = "";
    private String mRMN = "";
    private String mFcmToken;
    private String mParentDocId;
    private String mUid;

    @ServerTimestamp
    Date mTimeStamp;

    private List<String> mImagesUrl;

    public CommentPojo() {
    }

    public CommentPojo(String mCommentText, String mCompanyName, String mRMN, String mFcmToken, String mParentDocId,String mUid) {
        this.mCommentText = mCommentText;
        this.mCompanyName = mCompanyName;
        this.mRMN = mRMN;
        this.mFcmToken = mFcmToken;
        this.mParentDocId = mParentDocId;
        this.mUid = mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUid() {
        return mUid;
    }

    public String getmParentDocId() {
        return mParentDocId;
    }

    public void setmParentDocId(String mParentDocId) {
        this.mParentDocId = mParentDocId;
    }

    public String getmCommentText() {
        return mCommentText;
    }

    public void setmCommentText(String mCommentText) {
        this.mCommentText = mCommentText;
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

    public String getmFcmToken() {
        return mFcmToken;
    }

    public void setmFcmToken(String mFcmToken) {
        this.mFcmToken = mFcmToken;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public List<String> getmImagesUrl() {
        return mImagesUrl;
    }

    public void setmImagesUrl(List<String> mImagesUrl) {
        this.mImagesUrl = mImagesUrl;
    }

}
