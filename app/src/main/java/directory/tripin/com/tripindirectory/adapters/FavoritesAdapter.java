package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.response.Contact;

/**
 * Created by Yogesh N. Tikam on 29/09/2017.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Contact> mContactsList;
    private Context mContext;

    public FavoritesAdapter(Context context, List<Contact> contacts) {
        mContext = context;
        mContactsList = contacts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        final String companyName = mContactsList.get(position).getName();
        final String contactName = mContactsList.get(position).getContactName();
        final String mobileNo = mContactsList.get(position).getPhone();
        final String companyAddress = mContactsList.get(position).getAddress();

        itemViewHolder.mCompanyName.setText(companyName);
        itemViewHolder.mContact.setText(contactName + ":" + mobileNo);
        itemViewHolder.mAddress.setText(companyAddress);

        itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Uri.encode(mobileNo.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(callIntent);
            }
        });

        itemViewHolder.mAddToCommonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Contact contact1 = new Contact(companyName, com);
//                mContacts.add(contact1);
//                mPartnersList.getData().remove(directoryItemPosition);
                notifyDataSetChanged();

                insertContact(contactName, mobileNo);
            }
        });

        itemViewHolder.mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                insertFavorites(name, mobileNo, address);
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

                                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + companyAddress);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                if (mapIntent.resolveActivity(mContext.getPackageManager()) != null) {
                                    mContext.startActivity(mapIntent);
                                }
                                break;
                            case R.id.message_option:
                                //handle menu2 click

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


    private void insertContact(String contactName, String mobileNo) {

      /*  // Gets the data repository in write mode
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
        mSnackbar.show();*/

//            readDatabase();
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
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
