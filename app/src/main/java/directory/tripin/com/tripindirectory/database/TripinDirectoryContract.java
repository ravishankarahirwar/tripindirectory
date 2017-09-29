package directory.tripin.com.tripindirectory.database;

import android.provider.BaseColumns;

/**
 * Created by Yogesh N. Tikam on 15/9/2017.
 */

public final class TripinDirectoryContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TripinDirectoryContract() {
    }

    /* Inner class that defines the table contents */
    public static class DirectoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "directory";
        public static final String COLUMN_ENQUIRY = "enquiry";
        public static final String COLUMN_CONTACT_NO = "contact_number";
        public static final String COLUMN_CONTACT_NAME = "contact_name";
    }

    public class FavoritesEntry implements BaseColumns {
        public static final String FAVORITE_TABLE_NAME = "favorites";
        public static final String COLUMN_COMPANY_NAME = "company_name";
        public static final String COLUMN_COMPANY_PERSON= "company_person";
        public static final String COLUMN_COMPANY_CONTACT_NO = "company_contact_number";
        public static final String COLUMN_COMPANY_ADDRESS = "company_address";
    }
}
