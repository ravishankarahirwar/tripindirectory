package directory.tripin.com.tripindirectory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yogesh N. Tikam on 15/9/2017.
 */

public class TripinDirectoryDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TripinDirectory.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TripinDirectoryContract.DirectoryEntry.TABLE_NAME + " (" +
                    TripinDirectoryContract.DirectoryEntry._ID + " INTEGER PRIMARY KEY," +
                    TripinDirectoryContract.DirectoryEntry.COLUMN_ENQUIRY + " TEXT UNIQUE," +
                    TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NO + " TEXT," +
                    TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NAME + " TEXT" + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TripinDirectoryContract.DirectoryEntry.TABLE_NAME;

    public TripinDirectoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}
