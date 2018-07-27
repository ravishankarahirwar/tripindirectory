package directory.tripin.com.tripindirectory.NewLookCode

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

    init {

        mCardView = itemView.findViewById(R.id.partner_card_view)
        mAddress = itemView.findViewById(R.id.company_address)
        mCompany = itemView.findViewById(R.id.company_name1)
        mFleetInfo = itemView.findViewById(R.id.fleet_info)
        mCall = itemView.findViewById(R.id.call)

    }
}