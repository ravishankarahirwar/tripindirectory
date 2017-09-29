package directory.tripin.com.tripindirectory.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.database.TripinDirectoryContract;
import directory.tripin.com.tripindirectory.database.TripinDirectoryDbHelper;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.model.response.Contact;
import directory.tripin.com.tripindirectory.model.response.GetPartnersResponse;

/**
 * Created by Yogesh N. Tikam on 13/9/2017.
 */

public class PartnersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE = 0;
    private static final int CONTACT_TYPE = 1;
    private static final int DIRECTORY_TYPE = 2;

    private GetPartnersResponse mPartnersList;
    private List<Contact> mContacts;
    private Context mContext;

    private TripinDirectoryDbHelper mDbHelper;
    private String mEnquiry;

    private RelativeLayout mContentMainParent;

    private Snackbar mSnackbar;

    public PartnersAdapter(Context context, GetPartnersResponse partnersResponse, List<Contact> contacts, String enquiry, View parentView) {
        mPartnersList = partnersResponse;
        mContacts = contacts;
        mContext = context;
        mEnquiry = enquiry;
        mDbHelper = new TripinDirectoryDbHelper(mContext);
        mContentMainParent = (RelativeLayout) parentView;
        readDatabase();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(itemView);
        } else if (viewType == CONTACT_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
            if (position == 0) {
                if (mContacts.size() <= 0) {
                    sectionViewHolder.title.setText("Your Contacts match 0");
                } else {
                    sectionViewHolder.title.setText("Your Contacts");
                }
            } else {
                sectionViewHolder.title.setText("Directory Contact");
            }

        } else if (holder instanceof ContactViewHolder) {
            ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
            contactViewHolder.name.setText(mContacts.get(position - 1).getName());
            contactViewHolder.number.setText(mContacts.get(position - 1).getPhone());

            contactViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + Uri.encode(mContacts.get(position - 1).getPhone())));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(callIntent);
                }
            });

        } else {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final int directoryItemPosition = position - (mContacts.size() + 2);
            itemViewHolder.mCompanyName.setText(mPartnersList.getData().get(directoryItemPosition).getName());

            String contactName = mPartnersList.getData().get(directoryItemPosition).getContact().getName();
            String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();
            itemViewHolder.mContact.setText(contactName + ":" + mobileNo);

            itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contactName = mPartnersList.getData().get(directoryItemPosition).getContact().getName();
                    String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + Uri.encode(mobileNo.trim())));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(callIntent);
                }
            });
            itemViewHolder.mAddress.setText(mPartnersList.getData().get(directoryItemPosition).getAddress());

            itemViewHolder.mAddToCommonContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String contactName = mPartnersList.getData().get(directoryItemPosition).getName();
                    String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();

                    Contact contact1 = new Contact(contactName, mobileNo);
                    mContacts.add(contact1);
                    mPartnersList.getData().remove(directoryItemPosition);
                    notifyDataSetChanged();

                    insertContact(contactName, mobileNo);
                }
            });

            itemViewHolder.mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String companyName = mPartnersList.getData().get(directoryItemPosition).getName();
                    String address = mPartnersList.getData().get(directoryItemPosition).getAddress();
                    String contactPerson = mPartnersList.getData().get(directoryItemPosition).getContact().getName();
                    String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();

                    insertFavorites(companyName, contactPerson, mobileNo, address);
                }
            });

            itemViewHolder.mPopupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuBuilder menuBuilder = new MenuBuilder(mContext);
                    MenuInflater inflater = new MenuInflater(mContext);
                    inflater.inflate(R.menu.menu_popup, menuBuilder);
                    MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, view);
                    optionsMenu.setForceShowIcon(true);
                    // Set Item Click Listener
                    menuBuilder.setCallback(new MenuBuilder.Callback() {
                        @Override
                        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.location_option:
                                    //handle menu1 click
                                    String address = mPartnersList.getData().get(directoryItemPosition).getAddress();

                                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + address);
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                        mContext.startActivity(mapIntent);
                                    }
                                    break;
                                case R.id.message_option:
                                    //handle menu2 click
                                    String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();
                                    // Create the text message with a string
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_VIEW);
                                    sendIntent.putExtra("address", mobileNo);
                                    sendIntent.setType("vnd.android-dir/mms-sms");
                                    // Verify that the intent will resolve to an activity
                                    if (sendIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                        mContext.startActivity(sendIntent);
                                    }
                                    break;
                            }
                            return false;
                        }

                        @Override
                        public void onMenuModeChange(MenuBuilder menu) {
                        }
                    });
                    optionsMenu.show();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == mContacts.size() + 1) {
            return SECTION_TYPE;
        } else if (position > 0 && position <= mContacts.size()) {
            return CONTACT_TYPE;
        } else {
            return DIRECTORY_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        int totalItem = mContacts.size() + mPartnersList.getData().size() + 2;
        return totalItem;
    }

    private void insertContact(String contactName, String mobileNo) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TripinDirectoryContract.DirectoryEntry.COLUMN_ENQUIRY, mEnquiry);
        values.put(TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NO, mobileNo);
        values.put(TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NAME, contactName);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TripinDirectoryContract.DirectoryEntry.TABLE_NAME, null, values);
        Logger.v("New Row Id : " + newRowId);

        mSnackbar = Snackbar.make(mContentMainParent, R.string.contact_saved_snackbar_text, Snackbar.LENGTH_LONG);
        mSnackbar.show();

//            readDatabase();
    }

    private void readDatabase() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                TripinDirectoryContract.DirectoryEntry._ID,
                TripinDirectoryContract.DirectoryEntry.COLUMN_ENQUIRY,
                TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NO,
                TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NAME
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = TripinDirectoryContract.DirectoryEntry.COLUMN_ENQUIRY + " = ?";
        String[] selectionArgs = {mEnquiry};

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                TripinDirectoryContract.DirectoryEntry._ID + " DESC";

        Cursor cursor = db.query(
                TripinDirectoryContract.DirectoryEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NAME));
                    String contact = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.DirectoryEntry.COLUMN_CONTACT_NO));
                    String enquiry = cursor.getString(cursor.getColumnIndex(TripinDirectoryContract.DirectoryEntry.COLUMN_ENQUIRY));

                    Logger.v("db Name: " + name);
                    Logger.v("db Contact: " + contact);
                    Logger.v("db Enquiry: " + enquiry);

                    Contact databaseContact = new Contact(name, contact);
                    mContacts.add(databaseContact);
                }
                cursor.close();
            }
        }
    }


    private void insertFavorites(String companyName, String companyPerson, String companyContactNo, String companyAddress) {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_PERSON, companyPerson);
        values.put(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_NAME, companyName);
        values.put(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_CONTACT_NO, companyContactNo);
        values.put(TripinDirectoryContract.FavoritesEntry.COLUMN_COMPANY_ADDRESS, companyAddress);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TripinDirectoryContract.FavoritesEntry.FAVORITE_TABLE_NAME, null, values);
        Logger.v("New Row Id : " + newRowId);

        mSnackbar = Snackbar.make(mContentMainParent, R.string.contact_added_favorites_text, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public SectionViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.section_text);
        }
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView number;
        private ImageView mCall;

        public ContactViewHolder(View view) {
            super(view);
            mCall = itemView.findViewById(R.id.call);
            name = (TextView) view.findViewById(R.id.contact_name);
            number = (TextView) view.findViewById(R.id.contact_no);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mCompanyName;
        private TextView mContact;
        private TextView mAddress;
        private ImageView mCall;
        private ImageView mAddToCommonContact;
        private ImageView mFavorite;
        private ImageView mPopupMenu;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCompanyName = itemView.findViewById(R.id.company_name);
            mContact = itemView.findViewById(R.id.contact_no);
            mAddress = itemView.findViewById(R.id.address);
            mCall = itemView.findViewById(R.id.call);
            mFavorite = itemView.findViewById(R.id.favorite);
            mPopupMenu = itemView.findViewById(R.id.popup);
            mAddToCommonContact = itemView.findViewById(R.id.add);
        }
    }
}
