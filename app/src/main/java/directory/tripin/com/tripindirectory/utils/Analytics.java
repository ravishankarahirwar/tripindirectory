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
        String GO_TO_FORUM_BYNEWS = "go_to_forum_ByNews";
        String GO_TO_WEBSITE = "go_to_website";
        String GO_TO_YOUTUBE = "go_to_YouTube";
        String GO_TO_FACEBOOKPAGE = "go_to_FacebookPage";



    }

    interface Value {
        String CLICK = "Click";
    }
}
