package directory.tripin.com.tripindirectory.formactivities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.ContactPersonPojo;

/**
 * Created by Yogesh N. Tikam on 12/20/2017.
 */

public class ContactPersonsAdapter extends RecyclerView.Adapter<ContactPersonsAdapter.ViewHolder> {

    private List<ContactPersonPojo> mContactPersonsList;

    public ContactPersonsAdapter(List<ContactPersonPojo> mContactPersonsList) {
        this.mContactPersonsList = mContactPersonsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.include_add_person, parent, false);

        return new ContactPersonsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mPersonContactET.setText(mContactPersonsList.get(position).getGetmContactPersonMobile());
        holder.mPersonNameET.setText(mContactPersonsList.get(position).getmContactPresonName());

        holder.mRemovePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContactPersonsList.remove(position);
                notifyDataSetChanged();
            }
        });
//
//        holder.mPersonNameET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //modify the corrosponding obect in the list
//                mContactPersonsList.get(position)
//                        .setGetmContactPersonMobile(charSequence.toString().trim());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//
//        holder.mPersonContactET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                //modify the corrosponding obect in the list
//                mContactPersonsList.get(position)
//                        .setGetmContactPersonMobile(charSequence.toString().trim());
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mContactPersonsList.size();
    }

    public List<ContactPersonPojo> getmContactPersonsList() {
        return mContactPersonsList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public EditText mPersonNameET;
        public EditText mPersonContactET;
        public ImageView mRemovePerson;

        public ViewHolder(View itemView) {
            super(itemView);
            mPersonNameET = itemView.findViewById(R.id.contact_person_name);
            mPersonContactET = itemView.findViewById(R.id.contact_person_number);
            mRemovePerson = itemView.findViewById(R.id.removeperson);
        }
    }
}
