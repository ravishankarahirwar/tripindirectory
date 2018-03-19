package directory.tripin.com.tripindirectory.utils;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 18-03-2018
 */

public interface DB {
     interface Collection {
        String PARTNER = "partners";
         String QUERYBOOKMARKS = "mQueryBookmarks";
    }
    interface PartnerFields {
    }

    interface QueryBookmarkFields {
        String TIMESTAMP = "mTimeStamp";
    }
}
