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

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public ImageButton copyPost;
    public ImageView sharePost;
    public LinearLayout postTextContainer;
    public PostViewHolder(View itemView) {
        super(itemView);

        postTextContainer = (LinearLayout) itemView.findViewById(R.id.post_text_container);
        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        copyPost = (ImageButton) itemView.findViewById(R.id.copy);
        sharePost = (ImageButton) itemView.findViewById(R.id.share);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {

//        if (!TextUtils.isEmpty(post.title)) {
//            titleView.setVisibility(View.VISIBLE);
//            titleView.setText(post.title);
//        } else {
//            titleView.setVisibility(View.GONE);
//        }
//        authorView.setText(post.author);
//        numStarsView.setText(String.valueOf(post.starCount));
//        bodyView.setText(post.body);

        starView.setOnClickListener(starClickListener);
    }
}
