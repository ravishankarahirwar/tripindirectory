package directory.tripin.com.tripindirectory.newlookcode

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import directory.tripin.com.tripindirectory.R

class PartnersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mCardView: CardView
    var mAddress: TextView
    var mFleetInfo: TextView
    var mCompany: TextView
    var mCall :TextView
    var mChat :TextView
    var mIsSelectedImg : ImageView
    var mOnlineStatus : ImageView
    var mThumbnail : ImageView
    var mChatParent : ConstraintLayout
    var mRateLayout : LinearLayout
    var mRatings : TextView
    var mReviews : TextView


    var isSelected : Boolean

    init {
        mThumbnail = itemView.findViewById(R.id.transporter_thumbnail)
        mCardView = itemView.findViewById(R.id.partner_card_view)
        mAddress = itemView.findViewById(R.id.company_address)
        mCompany = itemView.findViewById(R.id.company_name1)
        mFleetInfo = itemView.findViewById(R.id.fleet_info)
        mCall = itemView.findViewById(R.id.call)
        mChat = itemView.findViewById(R.id.chat)
        mIsSelectedImg = itemView.findViewById(R.id.ischecked)
        mChatParent = itemView.findViewById(R.id.chatconstrain)
        isSelected = false
        mOnlineStatus = itemView.findViewById(R.id.onlinestatus)
        mRateLayout = itemView.findViewById(R.id.ratings)
        mRatings = itemView.findViewById(R.id.rating)
        mReviews = itemView.findViewById(R.id.reviews)


    }
}