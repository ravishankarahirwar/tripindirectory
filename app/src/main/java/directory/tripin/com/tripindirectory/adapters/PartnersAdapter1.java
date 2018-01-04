package directory.tripin.com.tripindirectory.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;
import directory.tripin.com.tripindirectory.manager.PartnersManager;
import directory.tripin.com.tripindirectory.manager.PreferenceManager;
import directory.tripin.com.tripindirectory.model.response.ElasticSearchResponse;
import directory.tripin.com.tripindirectory.model.response.LikeDislikeResponse;
import directory.tripin.com.tripindirectory.role.OnBottomReachedListener;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.CirclePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.CirclePromptFocal;

/**
 * Created by Yogesh N. Tikam on 11/16/2017.
 */

public class PartnersAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SECTION_TYPE_1 = 0;
    private static final int CONTACT_TYPE_1 = 1;
    private static final int DIRECTORY_TYPE_1 = 2;
    private static final int LOADING_TYPE = 3;
    private static final String UPVOTED = "1";
    private static final String DOWNVOTED = "-1";

    private Context mContext;
    private ElasticSearchResponse mElasticSearchResponse;
    private PreferenceManager mPreferenceManager;

    private PartnersManager mPartnersManager;

    private OnBottomReachedListener mOnBottomReachedListener;

    private ArrayList<String> mOrgIdList = new ArrayList<>();
    private ArrayList<String[]> mUserLikedList = new ArrayList<>();
    private ArrayList<String[]> mUserDislikedList = new ArrayList<>();

    private ArrayList< ElasticSearchResponse.PartnerData.Mobile[]> mMobileList = new ArrayList<>();
    private ArrayList<String> mCompanyList = new ArrayList<>();
    private ArrayList<String> mAddressList = new ArrayList<>();
    private ArrayList<String> mLikeList = new ArrayList<>();
    private ArrayList<String> mDislikeList = new ArrayList<>();

    private int mCount = 0;

    private boolean mIsLoading = true;


    public PartnersAdapter1(Context context, ElasticSearchResponse elasticSearchResponse) {
        mContext = context;
        mElasticSearchResponse = elasticSearchResponse;
        mPreferenceManager = PreferenceManager.getInstance(mContext);
        mPartnersManager = new PartnersManager(mContext);
        mOnBottomReachedListener = (OnBottomReachedListener) mContext;
        initLists();
    }

    public void addNewList( ElasticSearchResponse latestElasticSearchResponse) {
        mElasticSearchResponse =latestElasticSearchResponse;
        initLists();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SECTION_TYPE_1) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.section1, parent, false);
            return new SectionViewHolder(itemView);
        } else if (viewType == LOADING_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading_row1, parent, false);
            return new LoadingViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_partner_row1, parent, false);
            return new ItemViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SectionViewHolder) {
            SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;

            sectionViewHolder.title.setText("Directory");

        } else if (holder instanceof LoadingViewHolder){
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;

            if(mIsLoading) {
                loadingViewHolder.progressBar.setIndeterminate(true);
            } else {
                loadingViewHolder.progressBar.setVisibility(View.GONE);
            }

        }else {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            final String orgId = mOrgIdList.get(position - 1);

            final String[] userLiked = mUserLikedList.get(position - 1);
            final String[] userDisLiked = mUserDislikedList.get(position - 1);

            if (userLiked.length > 0) {
                for (String liked : userLiked) {
                    if (liked.equals(mPreferenceManager.getUserId())) {
                        itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                        itemViewHolder.mUpvote.setBackgroundResource(R.drawable.circle_shape);
                        itemViewHolder.isUpVoted = true;
                    }
                }
            } else {
                itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                itemViewHolder.mUpvote.setBackgroundResource(0);
                itemViewHolder.isUpVoted = false;
            }

            if (userDisLiked.length > 0) {
                for (String unLiked : userDisLiked) {
                    if (unLiked.equals(mPreferenceManager.getUserId())) {
                        itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                        itemViewHolder.mDownvote.setBackgroundResource(R.drawable.circle_shape);
                        itemViewHolder.isDownVoted = true;
                    }
                }
            } else {
                itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                itemViewHolder.mDownvote.setBackgroundResource(0);
                itemViewHolder.isDownVoted = false;
            }

            if (!mPreferenceManager.isFirstTime()) {
                itemViewHolder.mUpvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemViewHolder.isUpvoteClicked = true;
                        if (!itemViewHolder.isUpVoted) {
                            itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mUpvote.setBackgroundResource(R.drawable.circle_shape);
                            if (itemViewHolder.isDownVoted) {
                                itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                itemViewHolder.mDownvote.setBackgroundResource(0);
                                itemViewHolder.isDownVoted = false;
                            }
                            itemViewHolder.isUpVoted = true;
                            callLikeDislikeApi(orgId, UPVOTED, itemViewHolder);
                        } else {
                            itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mUpvote.setBackgroundResource(0);
                            itemViewHolder.isUpVoted = false;
                            callLikeDislikeApi(orgId, UPVOTED, itemViewHolder);
                        }
                    }
                });
            }

            if (!mPreferenceManager.isFirstTime()) {
                itemViewHolder.mDownvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemViewHolder.isUpvoteClicked = false;
                        if (!itemViewHolder.isDownVoted) {
                            itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), android.graphics.PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mDownvote.setBackgroundResource(R.drawable.circle_shape);
                            if (itemViewHolder.isUpVoted) {
                                itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                itemViewHolder.mUpvote.setBackgroundResource(0);
                                itemViewHolder.isUpVoted = false;
                            }
                            itemViewHolder.isDownVoted = true;
                            callLikeDislikeApi(orgId, DOWNVOTED, itemViewHolder);
                        } else {
                            itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mDownvote.setBackgroundResource(0);
                            itemViewHolder.isDownVoted = false;
                            callLikeDislikeApi(orgId, DOWNVOTED, itemViewHolder);
                        }
                    }
                });
            }

            itemViewHolder.mCompany.setText(mCompanyList.get(position - 1));
            itemViewHolder.mAddress.setText(mAddressList.get(position - 1));

            String strLike = mLikeList.get(position - 1);
            String strdisLike = mDislikeList.get(position - 1);

            int like = Integer.parseInt(strLike);
            int disLike = Integer.parseInt(strdisLike);

            itemViewHolder.mRanking.setText(String.valueOf(like - disLike));

            final ElasticSearchResponse.PartnerData.Mobile[] mMobileData = mMobileList.get(position - 1);

            if (!mPreferenceManager.isFirstTime()) {
                itemViewHolder.mCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ArrayList<String> phoneNumbers = new ArrayList<>();

                        if (mMobileData.length > 1) {

                            for (int i = 0; i < mMobileData.length; i++) {
                                phoneNumbers.add(mMobileData[i].getCellNo());
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("Looks like there are multiple phone numbers.")
                                    .setCancelable(false)
                                    .setAdapter(new ArrayAdapter<String>(mContext, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int item) {

                                                    Logger.v("Dialog number selected :" + phoneNumbers.get(item));

                                                    callNumber(phoneNumbers.get(item));
                                                }
                                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });

                            builder.create();
                            builder.show();
                        } else if (mMobileData.length == 1) {

                            callNumber(mMobileData[0].getCellNo());
                        } else {
                            Toast.makeText(mContext, "Mobile number not present for this contact", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            if (mPreferenceManager.isFirstTime()) {
                if (position == 2) {
                    upVoteTutorial(itemViewHolder);
                }
            }
        }
    }



    @Override
    public int getItemCount() {
        return mCount+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return SECTION_TYPE_1;
        } else if (position == mCount+1) {
            mOnBottomReachedListener.onBottomReached(position);
            return LOADING_TYPE;
        }else {
            return DIRECTORY_TYPE_1;
        }
    }

    private void callNumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(number.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }

    private void callLikeDislikeApi(String orgId, String vote, final ItemViewHolder itemViewHolder) {

        mPartnersManager.likeDislikeRequest(orgId, vote, new PartnersManager.LikeDislikeListener() {
            @Override
            public void onSuccess(LikeDislikeResponse likeDislikeResponse) {
                Logger.v("Like Dislike Api Success");


                Logger.v("Id : " + likeDislikeResponse.getData().get_id());
                final String[] userLiked = likeDislikeResponse.getData().getUserLiked();
                final String[] userDisLiked = likeDislikeResponse.getData().getUserDisliked();


                if (userLiked.length > 0) {
                    for (String liked : userLiked) {
                        if (liked.equals(mPreferenceManager.getUserId())) {
                            itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mUpvote.setBackgroundResource(R.drawable.circle_shape);
                            itemViewHolder.isUpVoted = true;
                        }
                    }
                } else {
                    itemViewHolder.mUpvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mUpvote.setBackgroundResource(0);
                    itemViewHolder.isUpVoted = false;
                }

                if (userDisLiked.length > 0) {
                    for (String unLiked : userDisLiked) {
                        if (unLiked.equals(mPreferenceManager.getUserId())) {
                            itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_white), PorterDuff.Mode.SRC_IN);
                            itemViewHolder.mDownvote.setBackgroundResource(R.drawable.circle_shape);
                            itemViewHolder.isDownVoted = true;
                        }
                    }
                } else {
                    itemViewHolder.mDownvote.setColorFilter(ContextCompat.getColor(mContext, R.color.arrow_grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    itemViewHolder.mDownvote.setBackgroundResource(0);
                    itemViewHolder.isDownVoted = false;
                }


                String strLike = likeDislikeResponse.getData().getLike();
                String strdisLike = likeDislikeResponse.getData().getDislike();

                int like = Integer.parseInt(strLike);
                int disLike = Integer.parseInt(strdisLike);

                itemViewHolder.mRanking.setText(String.valueOf(like - disLike));

            }

            @Override
            public void onFailed() {
                Logger.v("Like Dislike Api Failed");
            }
        });
    }

    private void callTutorial(ItemViewHolder itemViewHolder) {
        Logger.v("Inside call tutorial start");
        new MaterialTapTargetPrompt.Builder((Activity) mContext)
                .setPrimaryText("Call Button ")
                .setSecondaryText("Call the current partner")
                .setBackgroundColour(ContextCompat.getColor(mContext, R.color.primaryColor))
                .setPromptBackground(new CirclePromptBackground())
                .setPromptFocal(new CirclePromptFocal())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setTarget(itemViewHolder.mCall.getId())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                            //Do something such as storing a value so that this prompt is never shown again
                            prompt.finish();
                            mPreferenceManager.setFirstTime(false);
                            notifyDataSetChanged();
                        }
                    }
                }).show();

        Logger.v("Inside call tutorial end");
    }

    private void upVoteTutorial(final ItemViewHolder itemViewHolder) {
        new MaterialTapTargetPrompt.Builder((Activity) mContext)
                .setPrimaryText("Up-Vote button")
                .setSecondaryText("Increases the ranking of the current partner")
                .setBackgroundColour(ContextCompat.getColor(mContext, R.color.primaryColor))
                .setPromptBackground(new CirclePromptBackground())
                .setPromptFocal(new CirclePromptFocal())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setTarget(itemViewHolder.mUpvote.getId())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                            //Do something such as storing a value so that this prompt is never shown again
                            prompt.finish();
                            downVoteTutorial(itemViewHolder);
                        }
                    }
                }).show();
    }

    private void downVoteTutorial(final ItemViewHolder itemViewHolder) {
        Logger.v("Inside downvote tutorial start");
        new MaterialTapTargetPrompt.Builder((Activity) mContext)
                .setPrimaryText("Down-Vote button")
                .setSecondaryText("Decreases the ranking of the current partner")
                .setBackgroundColour(ContextCompat.getColor(mContext, R.color.primaryColor))
                .setPromptBackground(new CirclePromptBackground())
                .setPromptFocal(new CirclePromptFocal())
                .setAutoDismiss(false)
                .setAutoFinish(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setTarget(itemViewHolder.mDownvote.getId())
                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                    @Override
                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                        if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                            //Do something such as storing a value so that this prompt is never shown again
                            prompt.finish();
                            callTutorial(itemViewHolder);
//                            mPreferenceManager.setFirstTime(false);
                        }
                    }
                }).show();
    }

    public void stopLoad() {
            mIsLoading = false;
            notifyDataSetChanged();
    }

    private void initLists() {
        for (int i = 0; i < mElasticSearchResponse.getData().size(); i++) {

            mOrgIdList.add(mElasticSearchResponse.getData().get(i).get_id());
            mUserLikedList.add(mElasticSearchResponse.getData().get(i).getUserLiked());
            mUserDislikedList.add(mElasticSearchResponse.getData().get(i).getUserDisliked());
            mCompanyList.add(mElasticSearchResponse.getData().get(i).getName());
            mAddressList.add(mElasticSearchResponse.getData().get(i).getAddress());
            mMobileList.add(mElasticSearchResponse.getData().get(i).getMobile());
            mLikeList.add(mElasticSearchResponse.getData().get(i).getLike());
            mDislikeList.add(mElasticSearchResponse.getData().get(i).getDislike());
        }
        mCount = mCount + mElasticSearchResponse.getData().size();
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public SectionViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.section_text);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private ImageView mUpvote;
        private ImageView mDownvote;
        private TextView mRanking;
        private boolean isUpVoted;
        private boolean isDownVoted;
        private int mRank;
        private ExpandableTextView mAddress;
        private TextView mCall;
        private TextView mCompany;

        private boolean isUpvoteClicked;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.partner_card_view);

            mRank = 0;
            mCall = (TextView) itemView.findViewById(R.id.call);
            mAddress = (ExpandableTextView) itemView.findViewById(R.id.address);
            mCompany = (TextView) itemView.findViewById(R.id.company_name);
        }
    }
}
