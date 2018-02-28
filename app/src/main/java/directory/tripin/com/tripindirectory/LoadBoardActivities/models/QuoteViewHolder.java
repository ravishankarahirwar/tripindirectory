package directory.tripin.com.tripindirectory.LoadBoardActivities.models;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

/**
 * Created by Shubham on 2/25/2018.
 */

public class QuoteViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumb;
    public TextView quote,comment,call,inbox,time;
    public QuoteViewHolder(View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.imageViewThumbTyping);
        quote = itemView.findViewById(R.id.tv_quote);
        comment = itemView.findViewById(R.id.tv_comment);
        call = itemView.findViewById(R.id.textViewCall);
        inbox = itemView.findViewById(R.id.textViewInbox);
        time = itemView.findViewById(R.id.textViewTime);
    }
}
