package directory.tripin.com.tripindirectory.chatingactivities.models;

public class ChatIndicatorPojo {

    public String mLastMsgUserImageUrl = "";
    private Integer mMsgCount = 0;

    public ChatIndicatorPojo() {
    }

    public ChatIndicatorPojo(String mLastMsgUserImageUrl, Integer mMsgCount) {
        this.mLastMsgUserImageUrl = mLastMsgUserImageUrl;
        this.mMsgCount = mMsgCount;
    }

    public String getmLastMsgUserImageUrl() {
        return mLastMsgUserImageUrl;
    }

    public void setmLastMsgUserImageUrl(String mLastMsgUserImageUrl) {
        this.mLastMsgUserImageUrl = mLastMsgUserImageUrl;
    }

    public Integer getmMsgCount() {
        return mMsgCount;
    }

    public void setmMsgCount(Integer mMsgCount) {
        this.mMsgCount = mMsgCount;
    }
}
