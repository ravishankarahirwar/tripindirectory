package directory.tripin.com.tripindirectory.newlookcode.activities.fragments.loadboard;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.newlookcode.activities.fragments.loadboard.models.Post;
import directory.tripin.com.tripindirectory.R;


public class LoadPostViewHolder extends RecyclerView.ViewHolder {

    public TextView postType;
    public ImageView mThumbnail;
    public TextView auther;

    public TextView date;
    public TextView source;
    public TextView destination;
    public TextView material;
    public TextView truckType;
    public TextView bodyType;
    public TextView length;
    public TextView weight;
    public TextView postRequirement;


    public TextView call;
    public TextView chat;
    public TextView delete;
    public TextView sharePost;

    public LoadPostViewHolder(View itemView) {
        super(itemView);
        auther = itemView.findViewById(R.id.authername);
        mThumbnail = itemView.findViewById(R.id.thumb);
        postType =  itemView.findViewById(R.id.post_type);
        date = itemView.findViewById(R.id.date);
        source = itemView.findViewById(R.id.source);
        destination = itemView.findViewById(R.id.destination);
        material = itemView.findViewById(R.id.material);
        truckType = itemView.findViewById(R.id.truck_type);
        bodyType = itemView.findViewById(R.id.body_type);
        length = itemView.findViewById(R.id.length);
        weight = itemView.findViewById(R.id.weight);
        postRequirement = itemView.findViewById(R.id.post_requirement);

        sharePost =  itemView.findViewById(R.id.share);

        call = itemView.findViewById(R.id.call);
        chat = itemView.findViewById(R.id.chat);
        delete = itemView.findViewById(R.id.delete);

    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {

        if (post.mFindOrPost == 1) {
            postType.setText("Need Truck");
        }else if (post.mFindOrPost == 2) {
            postType.setText("Need Load");
        }
        if(post.getmAuthor()==null){
            auther.setVisibility(View.GONE);
        }else {
            auther.setVisibility(View.VISIBLE);
            auther.setText(post.getmAuthor());
        }

        date.setText(post.getmDate());
        source.setText(post.getmSource());
        destination.setText(post.getmDestination());
        material.setText(post.getmMeterial());

        if(post.getmTruckType()!=null){
            if(!post.getmTruckType().equals("Select Type of Vehicle")){
                truckType.setText(post.getmTruckType());
            }else {
                truckType.setText("");
            }
        }else {
            truckType.setText("");
        }

        if(post.getmTruckBodyType()!=null){
            if(!post.getmTruckBodyType().equals("Select Type of Body")){
                bodyType.setText(post.getmTruckBodyType());
            }else {
                bodyType.setText("");
            }
        }else {
            truckType.setText("");
        }

        length.setText(post.getmTruckLength());
        weight.setText(post.getmPayload());

        if(post.getmRemark() != null && post.getmRemark().trim().length() > 0) {
            postRequirement.setVisibility(View.VISIBLE);
            postRequirement.setText(post.getmRemark());
        } else {
            postRequirement.setVisibility(View.INVISIBLE);
        }



    }
}
