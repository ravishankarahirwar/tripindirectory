package directory.tripin.com.tripindirectory.adapters;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.interfaces.BookmarkListner;
import directory.tripin.com.tripindirectory.model.FilterPojo;
import directory.tripin.com.tripindirectory.model.QueryBookmarkPojo;
import directory.tripin.com.tripindirectory.utils.SearchBy;
import directory.tripin.com.tripindirectory.utils.ShortingType;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 18-03-2018
 */

public class BookmarkAdapter extends FirestoreRecyclerAdapter<QueryBookmarkPojo, QueryBookmarkViewHolder> {
    private BookmarkListner mCallback;
    public BookmarkAdapter(FirestoreRecyclerOptions<QueryBookmarkPojo> options, BookmarkListner callback) {
        super(options);
        mCallback = callback;
    }

    @Override
    protected void onBindViewHolder(final QueryBookmarkViewHolder holder, int position, final QueryBookmarkPojo model) {
                    holder.title.setText(model.getmBookmarkName());
                    holder.remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.remove.setText("Removing");
                            DocumentSnapshot snapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
                            snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Bookmark Removed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    holder.searchnow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mCallback.onBookMarkSearchClick(model);
                        }
                    });

                    if (model.getmSearchQuery().isEmpty()) {
                        holder.searchquery.setVisibility(View.GONE);
                    } else {

                        switch (model.getmSearchTag()) {
                            case SearchBy.SEARCHTAG_ROUTE: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_directions_grey_24dp),
                                        null,
                                        null,
                                        null);

                                break;
                            }
                            case SearchBy.SEARCHTAG_COMPANY: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_domain_black_24dp),
                                        null,
                                        null,
                                        null);
                                break;
                            }
                            case SearchBy.SEARCHTAG_CITY: {
                                holder.searchquery.setCompoundDrawablesWithIntrinsicBounds(ContextCompat
                                                .getDrawable(getApplicationContext(),
                                                        R.drawable.ic_location_on_black_24dp),
                                        null,
                                        null,
                                        null);
                                break;
                            }
                        }
                        holder.searchquery.setText(model.getmSearchQuery());
                    }

                    StringBuilder filterss = new StringBuilder();
                    for (FilterPojo f : model.getmFiltersList()) {
                        filterss.append(" (").append(f.getmFilterName()).append(") +");
                    }
                    if (!filterss.toString().isEmpty()) {
                        String s = filterss.substring(0, filterss.length() - 2);
                        holder.filters.setText(s);
                    } else {
                        holder.filters.setVisibility(View.GONE);
                    }

                    switch (model.getmSortIndex()) {
                        case 0: {
                            holder.sorting.setVisibility(View.GONE);
                            break;
                        }
                        case ShortingType.ALPHA_ASSENDING : {
                            holder.sorting.setText("Sorted Alphabetically A-Z");
                            break;
                        }
                        case ShortingType.ALPHA_DECENDING : {
                            holder.sorting.setText("Sorted Alphabetically Z-A");
                            break;
                        }
                        case  ShortingType.ACCOUNT_TYPE: {
                            holder.sorting.setText("Sorted By Crediblity");
                            break;
                        }
                    }
    }

    @Override
    public QueryBookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bookmark_query, parent, false);
        Logger.v("onCreat ViewHolder Bookmark");

        return new QueryBookmarkViewHolder(view);
    }
}
