package directory.tripin.com.tripindirectory.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.AddImage;
import directory.tripin.com.tripindirectory.model.ImageData;

/**
 * Created by Shubham on 12/7/2017.
 */

public class ImagesRecyclarAdapter extends RecyclerView.Adapter<ImagesRecyclarAdapter.ViewHolder> {

    public List<ImageData> list;
    AddImage addImage;
    Context context;

    public ImagesRecyclarAdapter(List<ImageData> list, AddImage addImage, Context context) {
        this.list = list;
        this.addImage = addImage;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.imageitem, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d("onBindViewHolder", position + "");

        String imageUrl;
        imageUrl = "";
        if (!list.get(position).getmImageUrl().isEmpty()){
            imageUrl = list.get(position).getmImageUrl();
        }
        if (!imageUrl.isEmpty())
        {
            Picasso.with(context).load(imageUrl).into(holder.imageButton);
        }


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pickup Image
                addImage.onPickClicked(position);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                addImage.onCancelClicked(position);
            }
        });

        String imageTitle = "";

        switch (position) {
            case 0:
                imageTitle = "Office";
                break;
            case 1:
                imageTitle = "Card";
                break;
            case 2:
                imageTitle = "Self";
                break;
        }

        holder.imageTitle.setText(imageTitle);

        if (list.get(position).getSet()||!imageUrl.isEmpty()) {
            holder.imageButton.setAlpha(1f);
            holder.imageButton.setImageBitmap(list.get(position).getmImageBitmap());
            holder.cancel.setVisibility(View.VISIBLE);
        } else {
            holder.imageButton.setAlpha(0.5f);
            holder.imageButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_circle_black_24dp));
            holder.cancel.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageButton;
        public ImageButton cancel;
        public TextView imageTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButton);
            cancel = itemView.findViewById(R.id.cancelbtn);
            imageTitle = itemView.findViewById(R.id.imageTitleTxt);
        }
    }
}
