package directory.tripin.com.tripindirectory.newlookcode.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

public class RecentCallsViewHolder extends RecyclerView.ViewHolder {

    public TextView name;
    public TextView rmn;
    public TextView time;
    public ImageView calltype_icon;

    public RecentCallsViewHolder(View itemView) {
        super(itemView);

        calltype_icon = itemView.findViewById(R.id.call_type_icon);
        name = itemView.findViewById(R.id.caller_compname);
        rmn = itemView.findViewById(R.id.caller_rmn);
        time = itemView.findViewById(R.id.callingtime);
    }
}
