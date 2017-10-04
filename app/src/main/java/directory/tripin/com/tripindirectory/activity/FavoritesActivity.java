package directory.tripin.com.tripindirectory.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.adapters.FavoritesAdapter;
import directory.tripin.com.tripindirectory.database.TripinDirectoryContract;
import directory.tripin.com.tripindirectory.database.TripinDirectoryDbHelper;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.response.Contact;

public class FavoritesActivity extends AppCompatActivity {

    private Context mContext;
    private RecyclerView mFavPartnerRecycler;
    private FavoritesAdapter mFavPartnerAdapter;
    private List<Contact> mFavContactsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mContext = FavoritesActivity.this;

        mFavPartnerRecycler = (RecyclerView) findViewById(R.id.fav_recycler);

        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mFavPartnerRecycler.setLayoutManager(verticalLayoutManager);

        getFavorites();

        mFavPartnerAdapter = new FavoritesAdapter(mContext, mFavContactsList);
        mFavPartnerRecycler.setAdapter(mFavPartnerAdapter);
    }

    private void getFavorites() {
        TripinDirectoryDbHelper dbHelper = new TripinDirectoryDbHelper(mContext);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TripinDirectoryContract.FavoritesEntry._ID,
                TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_NAME,
                TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_PERSON,
                TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_CONTACT_NO,
                TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_ADDRESS
        };

        Cursor cursor = db.query(
                TripinDirectoryContract.FavoritesEntry.FAVORITE_TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String companyName = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_NAME));
                    String contactName = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_PERSON));
                    String contactNo = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_CONTACT_NO));
                    String address = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_ADDRESS));

//                    Logger.v("favorites CompanyName: " + companyName);
//                    Logger.v("favorites ContactName: " + contactName);
//                    Logger.v("favorites Contact: " + contactNo);
//                    Logger.v("favorites Address: " + address);

                    Contact contact = new Contact(companyName, contactName, contactNo, address);
                    mFavContactsList.add(contact);
                }
                cursor.close();
            }
        }
    }
}
