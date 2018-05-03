package directory.tripin.com.tripindirectory.formactivities;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.List;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Yogesh N. Tikam on 12/20/2017.
 */

public class CompanyLandLineNumbersAdapter extends RecyclerView.Adapter<CompanyLandLineNumbersAdapter.ViewHolder> {

    private List<String> mCompanyLandLineNumbers;

    public CompanyLandLineNumbersAdapter(List<String> mCompanyLandLineNumbers) {
        this.mCompanyLandLineNumbers = mCompanyLandLineNumbers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.include_add_landline, parent, false);
        return new CompanyLandLineNumbersAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.removeLandline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCompanyLandLineNumbers.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.mLndlineET.setText(mCompanyLandLineNumbers.get(position));
    }

    @Override
    public int getItemCount() {
        return mCompanyLandLineNumbers.size();
    }

    public List<String> getmCompanyLandLineNumbers() {
        return mCompanyLandLineNumbers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public EditText mLndlineET;
        public ImageView removeLandline;
        public ViewHolder(View itemView) {
            super(itemView);
            mLndlineET = itemView.findViewById(R.id.landline_number);
            removeLandline = itemView.findViewById(R.id.imageViewRemoveLandline);
        }
    }
}
