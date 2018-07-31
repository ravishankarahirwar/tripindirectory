package directory.tripin.com.tripindirectory.forum.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.forum.models.Post;


public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView postType;

    public TextView date;
    public TextView source;
    public TextView destination;
    public TextView material;
    public TextView truckType;
    public TextView bodyType;
    public TextView length;
    public TextView weight;
    public TextView postRequirement;
    public TextView postRequirementCaption;

//
//    public ImageView postTypeIcon;
//    public ImageView call;
//    public ImageView chat;
//    public ImageView starView;
//    public ImageView delete;
//public ImageView sharePost;
//public ImageView comments;



    public TextView numStarsView;
    public TextView numCommentCount;
    public LinearLayout postTextContainer;
    public PostViewHolder(View itemView) {
        super(itemView);


        postTextContainer =  itemView.findViewById(R.id.post_text_container);
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
        postRequirementCaption = itemView.findViewById(R.id.post_requirement_caption);

        numCommentCount  = itemView.findViewById(R.id.comment_count);
        numStarsView = itemView.findViewById(R.id.post_num_stars);
//        comments =  itemView.findViewById(R.id.comment);
//        sharePost =  itemView.findViewById(R.id.share);
//
//        call = itemView.findViewById(R.id.call);
//        chat = itemView.findViewById(R.id.chat);
//        delete = itemView.findViewById(R.id.delete);
//        postTypeIcon =  itemView.findViewById(R.id.post_type_icon);
//        starView =  itemView.findViewById(R.id.star);

    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {

//        if (post.mFindOrPost == 1) {
//            postType.setText("Need Truck");
//            postTypeIcon.setImageResource(R.drawable.delivery_truck_front);
//        }else if (post.mFindOrPost == 2) {
//            postType.setText("Need Load");
//            postTypeIcon.setImageResource(R.drawable.ic_widgets_red_24dp);
//            material.setVisibility(View.GONE);
//        } else {
//            postType.setVisibility(View.GONE);
//        }

//        //date.setText(post.getmDate());
//        source.setText(post.getmSource());
//        destination.setText(post.getmDestination());
//        material.setText(post.getmMeterial());
//        truckType.setText(post.getmTruckType());
//        bodyType.setText(post.getmTruckBodyType());
//        length.setText(post.getmTruckLength());
//        weight.setText(post.getmPayload());
//
//        if(post.getmRemark() != null && post.getmRemark().trim().length() > 0) {
//            postRequirement.setVisibility(View.VISIBLE);
//            postRequirementCaption.setVisibility(View.VISIBLE);
//            postRequirement.setText(post.getmRemark());
//        } else {
//            postRequirement.setVisibility(View.GONE);
//            postRequirementCaption.setVisibility(View.GONE);
//        }
//
//        numStarsView.setText(String.valueOf(post.starCount));
//        numCommentCount.setText(String.valueOf(post.commentCount));
       // starView.setOnClickListener(starClickListener);
    }
}
