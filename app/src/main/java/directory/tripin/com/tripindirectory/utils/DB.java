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
        String LASTACTIVETIME = "mLastActiveTime";
        String COMPANY_NAME = "mCompanyName";
        String ACCOUNT_STATUS = "mAccountStatus";
    }

    interface QueryBookmarkFields {
        String TIMESTAMP = "mTimeStamp";
    }
}
