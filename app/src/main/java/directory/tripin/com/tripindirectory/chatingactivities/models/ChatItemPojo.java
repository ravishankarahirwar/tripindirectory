package directory.tripin.com.tripindirectory.chatingactivities.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Shubham on 2/25/2018.
 */

public class ChatItemPojo {

    private String mSendersUid;
    private String mReciversUid;
    private String mChatRoomId;
    private String mChatDocId;
    private String mSendersFuid;
    private String mImageUrl;
    private String mOpponentsImageUrl;
    private Boolean isSelected = false;


    private String mSendersFcmToken;
    private String mReciversFcmToken;

    private String mRMN;
    private String mORMN;
    private String mOFUID;

    private String mDisplayName = "";
    private String mOpponentsDisplayName = "";
    private String mChatMesssage;

    @ServerTimestamp
    Date mTimeStamp;

    private int mMessageStatus = 0;
    private int mMessageType = 0;

    public ChatItemPojo() {
    }

    public ChatItemPojo(String mSendersUid,
                        String mSendersFuid,
                        String mImageUrl,
                        String mOpponentsImageUrl,
                        String mReciversUid,
                        String mSendersFcmToken,
                        String mReciversFcmToken,
                        String mRMN,
                        String mORMN,
                        String mOFUID,
                        String mDisplayName,
                        String mOpponentsDisplayName,
                        String mChatMesssage,
                        String mChatRoomId,
                        int mMessageStatus,
                        int mMessageType) {

        this.mSendersUid = mSendersUid;
        this.mSendersFuid = mSendersFuid;
        this.mImageUrl = mImageUrl;
        this.mOpponentsImageUrl = mOpponentsImageUrl;
        this.mReciversUid = mReciversUid;
        this.mSendersFcmToken = mSendersFcmToken;
        this.mReciversFcmToken = mReciversFcmToken;
        this.mRMN = mRMN;
        this.mORMN = mORMN;
        this.mOFUID = mOFUID;
        this.mDisplayName = mDisplayName;
        this.mOpponentsDisplayName = mOpponentsDisplayName;
        this.mChatMesssage = mChatMesssage;
        this.mMessageStatus = mMessageStatus;
        this.mMessageType = mMessageType;
        this.mChatRoomId = mChatRoomId;
    }

    public String getmOpponentsDisplayName() {
        return mOpponentsDisplayName;
    }

    public void setmOpponentsDisplayName(String mOpponentsDisplayName) {
        this.mOpponentsDisplayName = mOpponentsDisplayName;
    }

    public String getmOpponentsImageUrl() {
        return mOpponentsImageUrl;
    }

    public void setmOpponentsImageUrl(String mOpponentsImageUrl) {
        this.mOpponentsImageUrl = mOpponentsImageUrl;
    }

    public String getmOFUID() {
        return mOFUID;
    }

    public void setmOFUID(String mOFUID) {
        this.mOFUID = mOFUID;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getmORMN() {
        return mORMN;
    }

    public void setmORMN(String mORMN) {
        this.mORMN = mORMN;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmSendersFuid() {
        return mSendersFuid;
    }

    public void setmSendersFuid(String mSendersFuid) {
        this.mSendersFuid = mSendersFuid;
    }

    public String getmChatRoomId() {
        return mChatRoomId;
    }

    public void setmChatRoomId(String mChatRoomId) {
        this.mChatRoomId = mChatRoomId;
    }

    public String getmSendersUid() {
        return mSendersUid;
    }

    public void setmSendersUid(String mSendersUid) {
        this.mSendersUid = mSendersUid;
    }

    public String getmReciversUid() {
        return mReciversUid;
    }

    public void setmReciversUid(String mReciversUid) {
        this.mReciversUid = mReciversUid;
    }

    public String getmSendersFcmToken() {
        return mSendersFcmToken;
    }

    public void setmSendersFcmToken(String mSendersFcmToken) {
        this.mSendersFcmToken = mSendersFcmToken;
    }

    public String getmReciversFcmToken() {
        return mReciversFcmToken;
    }

    public void setmReciversFcmToken(String mReciversFcmToken) {
        this.mReciversFcmToken = mReciversFcmToken;
    }

    public String getmRMN() {
        return mRMN;
    }

    public void setmRMN(String mRMN) {
        this.mRMN = mRMN;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public void setmDisplayName(String mDisplayName) {
        this.mDisplayName = mDisplayName;
    }

    public String getmChatMesssage() {
        return mChatMesssage;
    }

    public void setmChatMesssage(String mChatMesssage) {
        this.mChatMesssage = mChatMesssage;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public int getmMessageStatus() {
        return mMessageStatus;
    }

    public void setmMessageStatus(int mMessageStatus) {
        this.mMessageStatus = mMessageStatus;
    }

    public int getmMessageType() {
        return mMessageType;
    }

    public void setmMessageType(int mMessageType) {
        this.mMessageType = mMessageType;
    }
}
