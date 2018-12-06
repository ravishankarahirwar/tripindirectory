package directory.tripin.com.tripindirectory.newlookcode.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

public class RecentSearchesViewHolder extends RecyclerView.ViewHolder {

public TextView fromCity;
public TextView toCity;

public RecentSearchesViewHolder(View itemView) {
        super(itemView);

          fromCity = itemView.findViewById(R.id.fromcity);
          toCity = itemView.findViewById(R.id.tocity);


        }
}
