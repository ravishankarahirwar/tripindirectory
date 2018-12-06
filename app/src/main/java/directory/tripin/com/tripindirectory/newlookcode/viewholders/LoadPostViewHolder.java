package directory.tripin.com.tripindirectory.newlookcode.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;

public class LoadPostViewHolder extends RecyclerView.ViewHolder {

    public TextView source;
    public TextView destination;
    public TextView post_type;
    public  TextView authername;
    public ImageView thumb;

    public TextView truck_type;
    public TextView body_type;
    public TextView weight;
    public  TextView length;
    public  TextView material;
    public  TextView post_requirement;

    public TextView share;
    public  TextView chat;
    public  TextView call;
    public  TextView delete;
    public  TextView date;


    public LinearLayout loadpostDetails;
    public LinearLayout autherprofile;



    public LoadPostViewHolder(View itemView) {
        super(itemView);

        source = itemView.findViewById(R.id.source);
        destination = itemView.findViewById(R.id.destination);
        post_type = itemView.findViewById(R.id.post_type);
        authername = itemView.findViewById(R.id.authername);
        date = itemView.findViewById(R.id.date);

        thumb = itemView.findViewById(R.id.thumb);

        truck_type = itemView.findViewById(R.id.truck_type);
        body_type = itemView.findViewById(R.id.body_type);
        weight = itemView.findViewById(R.id.weight);
        length = itemView.findViewById(R.id.length);
        material = itemView.findViewById(R.id.material);
        post_requirement = itemView.findViewById(R.id.post_requirement);

        share = itemView.findViewById(R.id.share);
        chat = itemView.findViewById(R.id.chat);
        call = itemView.findViewById(R.id.call);
        delete = itemView.findViewById(R.id.delete);

        loadpostDetails = itemView.findViewById(R.id.loadpostdetails);
        autherprofile = itemView.findViewById(R.id.autherprofile);
    }
}