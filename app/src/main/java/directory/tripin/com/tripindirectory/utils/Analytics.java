package directory.tripin.com.tripindirectory.utils;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 19-03-2018
 */

public interface Analytics {
    interface Event {
        String LOGOUT = "logout";
        String SHARE = "share";
        String FEEDBACK = "feedback";
        String INVITE = "invite";
    }

    interface Value {
        String CLICK = "Click";
    }
}
