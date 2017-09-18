package directory.tripin.com.tripindirectory.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
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
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
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

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCompanyName = itemView.findViewById(R.id.company_name);
            mContact = itemView.findViewById(R.id.contact_no);
            mAddress = itemView.findViewById(R.id.address);
            mCall = itemView.findViewById(R.id.call);
            mAddToCommonContact = itemView.findViewById(R.id.add);
        }
    }

}
