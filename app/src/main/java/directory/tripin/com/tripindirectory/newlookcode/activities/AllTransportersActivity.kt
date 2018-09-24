package directory.tripin.com.tripindirectory.newlookcode.activities

import android.arch.paging.PagedList
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.newlookcode.BasicQueryPojo
import directory.tripin.com.tripindirectory.newlookcode.PartnersViewHolder
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.models.CompanyCardPojo
import directory.tripin.com.tripindirectory.utils.DB
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.activity_all_transporters.*
import kotlinx.android.synthetic.main.content_main_scrolling.*
import libs.mjn.prettydialog.PrettyDialog
import libs.mjn.prettydialog.PrettyDialogCallback

class AllTransportersActivity : AppCompatActivity() {

    lateinit var adapter: FirestorePagingAdapter<CompanyCardPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo
    lateinit var context: Context
    lateinit var textUtils: TextUtils
    lateinit var preferenceManager :PreferenceManager
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var  recyclerViewAnimator: RecyclerViewAnimator




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transporters)
        context = this
        textUtils = TextUtils()
        preferenceManager = PreferenceManager.getInstance(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        recyclerViewAnimator = RecyclerViewAnimator(rv_transporters_at)

        if(intent.extras!=null){
            if(intent.extras.getSerializable("query")!=null){
                basicQueryPojo = intent.extras.getSerializable("query") as BasicQueryPojo
                if(!basicQueryPojo.mSourceCity.isEmpty()&&!basicQueryPojo.mDestinationCity.isEmpty()){
                    title = "${textUtils.toTitleCase(basicQueryPojo.mSourceCity)} To ${textUtils.toTitleCase(basicQueryPojo.mDestinationCity)}"
                }else{
                    title = "All Transporters"
                }
                var fleets: String = ""
                for(fleet:String in basicQueryPojo.mFleets!!){
                    fleets = "$fleets,$fleet"
                }
                if(!fleets.isEmpty()){
                    supportActionBar!!.subtitle = fleets.substring(1)
                }else{
                    supportActionBar!!.subtitle = fleets
                }
                setMainAdapter(basicQueryPojo)
            }
        }

        fabfilter.setOnClickListener {
            shownewlookfeedbackdialog()
            Toast.makeText(context,"Feature Coming Soon",Toast.LENGTH_SHORT).show()
        }

        internetCheck()


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_alltrans, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_cancel -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun setMainAdapter( basicQueryPojo: BasicQueryPojo) {

        val bundle = Bundle()
        Logger.v(basicQueryPojo.toString())

        var source = "ANYWHERE"
        if (!basicQueryPojo.mSourceCity.isEmpty() && basicQueryPojo.mSourceCity != "Select City") {
            bundle.putString("source", basicQueryPojo.mSourceCity)
            source = basicQueryPojo.mSourceCity.toUpperCase()
        } else {
            bundle.putString("source", "Empty")
        }

        var destination = "ANYWHERE"
        if (!basicQueryPojo.mDestinationCity.isEmpty() && basicQueryPojo.mDestinationCity != "Select City") {
            bundle.putString("destination", basicQueryPojo.mDestinationCity)
            destination = basicQueryPojo.mDestinationCity.toUpperCase()
        } else {
            bundle.putString("destination", "Empty")
        }

        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("denormalised")
                .document("routes")
                .collection(source)
                .document(destination)
                .collection("companies")

        var numberofFleets: Int = 0

        var fleetssorter = ""
        var list = basicQueryPojo.mFleets!!
        list.sort()
        Logger.v("Selected Fleets : $list")
        for (fleet in list) {
            fleetssorter = fleetssorter+fleet+"_"
        }
        Logger.v("mFleetsSorter: $fleetssorter")
        bundle.putInt("fleetsselected", numberofFleets)
        firebaseAnalytics.logEvent("z_set_main_adapter", bundle)

        //fitler and sort
        baseQuery = baseQuery.whereArrayContains("mDetails.mFleetsSort",fleetssorter)
        baseQuery = baseQuery.orderBy("mBidValue",Query.Direction.DESCENDING)
        baseQuery =  baseQuery.orderBy("mDetails.mLastActive", Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.mAvgRating",Query.Direction.DESCENDING)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<CompanyCardPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, CompanyCardPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<CompanyCardPojo, PartnersViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_transporter, parent, false)
                recyclerViewAnimator.onCreateViewHolder(view)
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: CompanyCardPojo) {

                if (model != null) {
                    recyclerViewAnimator.onBindViewHolder(holder.itemView,position)

                    //CompName
                    if (model.getmDetails().getmCompanyName() != null) {
                        if (!model.getmDetails().getmCompanyName().isEmpty()) {
                            holder.mCompany.text = textUtils.toTitleCase(model.getmDetails().getmCompanyName())
                        } else {
                            if(model.getmDetails().getmDisplayName()!=null){
                                if(!model.getmDetails().getmDisplayName().isEmpty()){
                                    holder.mCompany.text = model.getmDetails().getmDisplayName()
                                }else{
                                    holder.mCompany.text = "Unknown Name"
                                }
                            }
                        }
                    } else {
                        if(model.getmDetails().getmDisplayName()!=null){
                            if(!model.getmDetails().getmDisplayName().isEmpty()){
                                holder.mCompany.text = model.getmDetails().getmDisplayName()
                            }else{
                                holder.mCompany.text = "Unknown Name"
                            }
                        }
                    }


                    //City
                    if (model.getmDetails().getmLocationCity() != null){
                        if(model.getmDetails().getmLocationCity()!=null){
                            holder.mAddress.text = textUtils.toTitleCase(model.getmDetails().getmLocationCity())
                        }
                    }


                    //Photo
                    if (model.getmDetails().getmPhotoUrl() != null) {
                        if (!model.getmDetails().getmPhotoUrl().isEmpty()) {
                            Picasso.with(applicationContext)
                                    .load(model.getmDetails().getmPhotoUrl()+ "?width=100&width=100")
                                    .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                                    .transform(CircleTransform())
                                    .fit()
                                    .into(holder.mThumbnail, object : Callback {

                                        override fun onSuccess() {
                                            Logger.v("image set: transporter thumb")
                                        }

                                        override fun onError() {
                                            Logger.v("image transporter Error")
                                        }
                                    })
                        }

                    } else {
                        holder.mThumbnail.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.emoji_google_category_travel))
                    }

//                    if(model.getmAccountStatus()!=null){
//                        if(model.getmAccountStatus()>=2){
//                            holder.mThumbnail.background = ContextCompat.getDrawable(context,R.drawable.border_sreoke_yollo_bg)
//                        }else{
//                            holder.mThumbnail.background = ContextCompat.getDrawable(context,R.drawable.border_stroke_bg)
//                        }
//                    }

                    if(model.getmDetails().isActive!=null){
                        if(model.getmDetails().isActive){
                            Logger.v("active..")
                            holder.mOnlineStatus.setColorFilter(ContextCompat.getColor(context,R.color.green_A200),android.graphics.PorterDuff.Mode.SRC_IN)
                        }else{
                            Logger.v("inactive..")
                            holder.mOnlineStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_panorama_fish_eye_black_24dp))
                        }
                    }else{
                        Logger.v("null..")
                        holder.mOnlineStatus.setColorFilter(ContextCompat.getColor(context,R.color.gray2),android.graphics.PorterDuff.Mode.SRC_IN)

                    }

                    if(model.getmDetails().getmAvgRating()!=null){
                        holder.mRatings.text = model.getmDetails().getmAvgRating().toString()
                    }

                    if(model.getmDetails().getmNumRatings()!=null){
                        holder.mReviews.text = model.getmDetails().getmNumRatings().toInt().toString() + " reviews"
                    }

                    if(model.getmBidValue()!=null){
                        if(model.getmBidValue() != 0.0){
                            holder.mIsPromoted.visibility = View.VISIBLE
                        }else{
                            holder.mIsPromoted.visibility = View.GONE
                        }
                    }



                    holder.itemView.setOnClickListener {

                        val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                        i.putExtra("uid",getItem(position)!!.id)
                        i.putExtra("rmn",model.getmDetails().getmRMN())
                        i.putExtra("fuid",model.getmDetails().getmFUID())
                        startActivity(i)

                    }

                    holder.mCall.setOnClickListener {

                        if(model.getmDetails().getmRMN()!=null) {
                            callNumber(model.getmDetails().getmRMN())
                        }else {
                            Toast.makeText(context,"No RMN, Visit Profile!",Toast.LENGTH_SHORT).show()
                        }

                        val bundle = Bundle()

                        if (!basicQueryPojo.mSourceCity.isEmpty() &&
                                basicQueryPojo.mSourceCity != "Select City" &&
                                !basicQueryPojo.mDestinationCity.isEmpty() &&
                                basicQueryPojo.mDestinationCity != "Select City") {
                            bundle.putString("is_route_queried", "Yes")
                        } else {
                            bundle.putString("is_route_queried", "No")
                        }

                        bundle.putInt("fleets_queried", basicQueryPojo.mFleets!!.size)

                        firebaseAnalytics.logEvent("z_call_clicked_pl", bundle)
                    }

                    holder.mChatParent.setOnClickListener {

                        val intent = Intent(context, ChatRoomActivity::class.java)
                        intent.putExtra("imsg", basicQueryPojo.toString())
                        intent.putExtra("ormn", model.getmDetails().getmRMN())
                        intent.putExtra("ouid", getItem(position)!!.id)
                        intent.putExtra("ofuid", model.getmDetails().getmFUID())
                        Logger.v("Ofuid :" + model.getmDetails().getmFUID())
                        startActivity(intent)

                        val bundle = Bundle()
                        if (preferenceManager.rmn != null) {
                            bundle.putString("by_rmn", preferenceManager.rmn)
                        } else {
                            bundle.putString("by_rmn", "Unknown")
                        }
                        bundle.putString("to_rmn", model.getmDetails().getmRMN())
                        if (model.getmDetails().getmFUID() != null) {
                            bundle.putString("is_opponent_updated", "Yes")
                        } else {
                            bundle.putString("is_opponent_updated", "No")
                        }
                        if (!basicQueryPojo.mSourceCity.isEmpty() &&
                                basicQueryPojo.mSourceCity != "Select City" &&
                                !basicQueryPojo.mDestinationCity.isEmpty() &&
                                basicQueryPojo.mDestinationCity != "Select City") {
                            bundle.putString("is_route_queried", "Yes")
                        } else {
                            bundle.putString("is_route_queried", "No")
                        }
                        bundle.putInt("fleets_queried", basicQueryPojo.mFleets!!.size)
                        firebaseAnalytics.logEvent("z_chat_clicked_pl", bundle)

                    }
                }


            }


            override fun getItemViewType(position: Int): Int {

                return if (position == itemCount - 1) {
                    0
                } else {
                    0
                }
            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingat.visibility = View.VISIBLE
                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingat.visibility = View.VISIBLE
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingat.visibility = View.GONE

                    }

                    LoadingState.FINISHED ->{
                        loadingat.visibility = View.GONE
                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        rv_transporters_at.layoutManager = LinearLayoutManager(this)
        rv_transporters_at.adapter = adapter

    }

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(callIntent)
    }

    private fun shownewlookfeedbackdialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle("Filters and Sorting")
                .setMessage("This feature is still in development, do you have any suggestion about how it should be?")
                .addButton(
                        "Yes, Feedback to Assistant",
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    chatwithassistant()

                }.addButton(
                        "Cancel",
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100,
                        PrettyDialogCallback {
                            prettyDialog.dismiss()

                        }
                )
        prettyDialog.show()

        val bundle = Bundle()
        if (preferenceManager.displayName == null) {
            bundle.putString("iswithname", "No")
        } else {
            bundle.putString("iswithname", "Yes")
        }
        firebaseAnalytics.logEvent("z_respond_clicked", bundle)
    }

    private fun chatwithassistant() {
        val intent = Intent(this@AllTransportersActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("imsg", "Hi, This is my suggestion/requirement for the filter/sor feature in transporters list.")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)
    }

    /**
     * This method is use for checking internet connectivity
     * If there is no internet it will show an snackbar to user
     */
    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }
}
