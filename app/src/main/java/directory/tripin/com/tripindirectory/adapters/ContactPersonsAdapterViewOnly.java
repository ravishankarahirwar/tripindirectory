package directory.tripin.com.tripindirectory.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;

/**
 * Created by Yogesh N. Tikam on 12/20/2017.
 */

public class ContactPersonsAdapterViewOnly extends RecyclerView.Adapter<ContactPersonsAdapterViewOnly.ViewHolder> {

    private List<ContactPersonPojo> mContactPersonsList;

    public ContactPersonsAdapterViewOnly(List<ContactPersonPojo> mContactPersonsList) {
        this.mContactPersonsList = mContactPersonsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_person_viewonly, parent, false);

        return new ContactPersonsAdapterViewOnly.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ContactPersonPojo contactPersonPojo = mContactPersonsList.get(position);
        if(contactPersonPojo != null) {
            String name = contactPersonPojo.getmContactPresonName();
            String number = contactPersonPojo.getGetmContactPersonMobile();

            holder.mPersonNameET.setText(name != null ? name : "");
            holder.mPersonContactET.setText(number != null ? number : "Not Found");
        }

    }

    @Override
    public int getItemCount() {
        return mContactPersonsList.size();
    }

    public List<ContactPersonPojo> getmContactPersonsList() {
        return mContactPersonsList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mPersonNameET;
        public TextView mPersonContactET;

        public ViewHolder(View itemView) {
            super(itemView);
            mPersonNameET = itemView.findViewById(R.id.contact_person_name);
            mPersonContactET = itemView.findViewById(R.id.contact_person_number);
        }
    }
}
