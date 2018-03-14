package directory.tripin.com.tripindirectory.ChatingActivities.models;

/**
 * Created by Shubham on 2/27/2018.
 */

public class UserPresensePojo {

    private Boolean isActive = false;
    private Long mTimeStamp;
    private String mChatroomId;

    public UserPresensePojo() {
    }

    public UserPresensePojo(Boolean isActive, Long mTimeStamp, String mChatroomId) {
        this.isActive = isActive;
        this.mTimeStamp = mTimeStamp;
        this.mChatroomId = mChatroomId;
    }

    public String getmChatroomId() {
        return mChatroomId;
    }

    public void setmChatroomId(String mChatroomId) {
        this.mChatroomId = mChatroomId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }


    public Long getmTimeStamp() {
        return mTimeStamp;
    }

    public void setmTimeStamp(Long mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }
}
