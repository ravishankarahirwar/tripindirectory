package directory.tripin.com.tripindirectory.chatingactivities.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by Shubham on 2/25/2018.
 */

public class ChatHeadPojo {

    String mChatRoomId;
    String mORMN;
    String mOUID;
    String mOFUID;
    String mLastMessage;
    String mOpponentCompanyName;

    @ServerTimestamp
    Date mTimeStamp;

    String mOpponentImageUrl;

    public ChatHeadPojo() {
    }

    public ChatHeadPojo(String mChatRoomId, String mORMN, String mOUID,String mOFUID, String mLastMessage, String mOpponentImageUrl, String mOpponentCompanyName) {
        this.mChatRoomId = mChatRoomId;
        this.mORMN = mORMN;
        this.mOUID = mOUID;
        this.mOFUID = mOFUID;
        this.mLastMessage = mLastMessage;
        this.mOpponentImageUrl = mOpponentImageUrl;
        this.mOpponentCompanyName = mOpponentCompanyName;
    }

    public String getmOFUID() {
        return mOFUID;
    }

    public void setmOFUID(String mOFUID) {
        this.mOFUID = mOFUID;
    }

    public String getmChatRoomId() {
        return mChatRoomId;
    }

    public String getmOpponentCompanyName() {
        return mOpponentCompanyName;
    }

    public void setmOpponentCompanyName(String mOpponentCompanyName) {
        this.mOpponentCompanyName = mOpponentCompanyName;
    }

    public void setmChatRoomId(String mChatRoomId) {
        this.mChatRoomId = mChatRoomId;
    }

    public String getmORMN() {
        return mORMN;
    }

    public void setmORMN(String mORMN) {
        this.mORMN = mORMN;
    }

    public String getmOUID() {
        return mOUID;
    }

    public void setmOUID(String mOUID) {
        this.mOUID = mOUID;
    }

    public String getmLastMessage() {
        return mLastMessage;
    }

    public void setmLastMessage(String mLastMessage) {
        this.mLastMessage = mLastMessage;
    }

    public Date getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Date mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    public String getmOpponentImageUrl() {
        return mOpponentImageUrl;
    }

    public void setmOpponentImageUrl(String mOpponentImageUrl) {
        this.mOpponentImageUrl = mOpponentImageUrl;
    }
}
