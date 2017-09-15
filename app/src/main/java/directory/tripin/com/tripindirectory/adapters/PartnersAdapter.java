package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import directory.tripin.com.tripindirectory.R;
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

    public PartnersAdapter(Context context, GetPartnersResponse partnersResponse, List<Contact> contacts) {
        mPartnersList = partnersResponse;
        mContacts = contacts;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SECTION_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section, parent, false);
            return new SectionViewHolder(itemView);
        } else if(viewType == CONTACT_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
            return new ContactViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof SectionViewHolder) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder)holder;
            if (position == 0 ) {
                sectionViewHolder.title.setText("Your Contacts");
            } else {
                sectionViewHolder.title.setText("Directory Contact");
            }

        }else if(holder instanceof ContactViewHolder) {
            ContactViewHolder contactViewHolder = (ContactViewHolder)holder;
            contactViewHolder.name.setText(mContacts.get(position-1).getName());
            contactViewHolder.number.setText(mContacts.get(position-1).getPhone());
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            final int directoryItemPosition = position - (mContacts.size() + 2);
            itemViewHolder.mCompanyName.setText(mPartnersList.getData().get(directoryItemPosition).getName());

            String contactName = mPartnersList.getData().get(directoryItemPosition).getContact().getName();
            String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();
            itemViewHolder.mContact.setText(contactName + ":" + mobileNo);

//            String mobileNo = mPartnersList.getData().get(directoryItemPosition).getMobile();
//            Pattern pattern = Pattern.compile("\\d{10}");
//            Matcher matcher = pattern.matcher(mobileNo);
//            if (matcher.find()) {
//                mobileNo = matcher.group(0);
//                itemViewHolder.mContact.setText(mobileNo);
//            } else {
//                itemViewHolder.mContact.setText(mPartnersList.getData().get(directoryItemPosition).getMobile());
//            }

            itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contactName = mPartnersList.getData().get(directoryItemPosition).getContact().getName();
                    String mobileNo = mPartnersList.getData().get(directoryItemPosition).getContact().getContact();
//                    Pattern pattern = Pattern.compile("\\d{10}");
//                    Matcher matcher = pattern.matcher(contactName + " : " + mobileNo);
//                    if (matcher.find()) {
//                        mobileNo = matcher.group(0);
//                    }
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

                    Contact contact1 = new Contact(contactName , mobileNo);
                    mContacts.add(contact1);
                    mPartnersList.getData().remove(directoryItemPosition);
                    notifyDataSetChanged();

//                    Intent addContact = new Intent(Intent.ACTION_INSERT);
//                    addContact.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//                    addContact.putExtra(ContactsContract.Intents.Insert.COMPANY,mPartnersList.getData().get(position).getName()); //Company Name
//                    addContact.putExtra(ContactsContract.Intents.Insert.PHONE, mPartnersList.getData().get(position).getContact().getContact());
//                    addContact.putExtra(ContactsContract.Intents.Insert.NAME, mPartnersList.getData().get(position).getContact().getName());//Contact Name
//                    mContext.startActivity(addContact);
                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || position == mContacts.size()+1) {
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

        public ContactViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.contact_name);
            number = (TextView) view.findViewById(R.id.contact_no);
        }
    }
}
