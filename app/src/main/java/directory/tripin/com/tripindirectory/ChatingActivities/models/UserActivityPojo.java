package directory.tripin.com.tripindirectory.ChatingActivities.models;

/**
 * Created by Shubham on 2/27/2018.
 */

public class UserActivityPojo {

    Boolean isTyping = false;
    //Boolean isAudioRecording = false;


    public UserActivityPojo() {
    }

    public UserActivityPojo(Boolean isTyping) {
        this.isTyping = isTyping;
    }

    public Boolean getTyping() {
        return isTyping;
    }

    public void setTyping(Boolean typing) {
        isTyping = typing;
    }
}
