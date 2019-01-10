package directory.tripin.com.tripindirectory.newlookcode.activities

import android.app.Activity
import android.app.NotificationManager
import android.arch.paging.PagedList
import android.content.ActivityNotFoundException
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.PartnersViewHolder
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.viewholders.LoadPostViewHolder
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.models.CompanyCardPojo
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import directory.tripin.com.tripindirectory.newprofiles.models.RateReminderPojo
import kotlinx.android.synthetic.main.activity_all_transporters.*
import kotlinx.android.synthetic.main.activity_fsload_board.*
import kotlinx.android.synthetic.main.layout_fsloadboard_actionbar.*
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class FSLoadBoardActivity : AppCompatActivity() {

    internal var LOADBOARD_FILTER_REQUEST_CODE = 1
    lateinit var filterLoadPostPojo: LoadPostPojo
    lateinit var adapter: FirestorePagingAdapter<LoadPostPojo, LoadPostViewHolder>
    lateinit var recyclerViewAnimator: RecyclerViewAnimator
    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics: FirebaseAnalytics
    var lastloadid : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fsload_board)
        context = this
        recyclerViewAnimator = RecyclerViewAnimator(fsloadslist)
        preferenceManager = PreferenceManager.getInstance(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

        setListners()

        filterLoadPostPojo = LoadPostPojo()
        arrangeUIaccordingtofilters()
        internetCheck()



    }

    override fun onResume() {
        super.onResume()

        if(intent.extras.getString("loadid")!=null){

            if(lastloadid!=intent.extras.getString("loadid")){
                val i = Intent(context, SingleLoadDetailsActivity::class.java)
                i.putExtra("loadid",intent.extras.getString("loadid"))
                lastloadid = intent.extras.getString("loadid")
                startActivity(i)
            }

        }


    }

    private fun arrangeUIaccordingtofilters() {

        if (preferenceManager.prefLBFilter.isNotEmpty()) {
            clearfilters.visibility = View.VISIBLE
            filterdetails.text = getString(R.string.filters_are_added)
            filterloads.text = getString(R.string.edit_filters)
            filterLoadPostPojo = Gson().fromJson(preferenceManager.prefLBFilter, LoadPostPojo::class.java)
            setAdapter(filterLoadPostPojo)
        } else {
            clearfilters.visibility = View.GONE
            filterdetails.text = getString(R.string.no_filters_are_added)
            filterloads.text = getString(R.string.add_filters)
            filterLoadPostPojo = LoadPostPojo()
            setAdapter(filterLoadPostPojo)
        }

    }


    private fun setListners() {
        manageloads.setOnClickListener {
            val i = Intent(this, ManageLoadsActivity::class.java)
            startActivity(i)
        }

        backfslb.setOnClickListener {
            finish()
        }

        filterloads.setOnClickListener {
            val i = Intent(this, LoadFiltersActivity::class.java)
            startActivityForResult(i, LOADBOARD_FILTER_REQUEST_CODE)
        }

        clearfilters.setOnClickListener {
            preferenceManager.prefLBFilter = ""
            arrangeUIaccordingtofilters()
        }

        fablbsync.setOnClickListener {
            setAdapter(filterLoadPostPojo)
        }
        swiperefresh.setOnRefreshListener {
            swiperefresh.isRefreshing = false
            setAdapter(filterLoadPostPojo)
        }

    }


    private fun setAdapter(filterLoadPostPojo: LoadPostPojo) {

        val bundle = Bundle()
        Logger.v(filterLoadPostPojo.toString())


        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("loadposts")


        //time sort
        baseQuery = baseQuery.orderBy("mTimeStamp", Query.Direction.DESCENDING)

        //other filters

        if (filterLoadPostPojo.getmSourceHub() != null) {
            baseQuery = baseQuery.whereEqualTo("mSourceHub", filterLoadPostPojo.getmSourceHub())
        }


        if (filterLoadPostPojo.getmDestinationHub() != null) {
            Logger.v(filterLoadPostPojo.getmDestinationHub())
            baseQuery = baseQuery.whereEqualTo("mDestinationHub", filterLoadPostPojo.getmDestinationHub())
        }

        if (filterLoadPostPojo.getmPayload() != null) {
            if (filterLoadPostPojo.getmPayload().isNotEmpty()) {
                baseQuery = baseQuery.whereEqualTo("mPayload", filterLoadPostPojo.getmPayload())
                if (filterLoadPostPojo.getmPayloadUnit() != null) {
                    baseQuery = baseQuery.whereEqualTo("mPayloadUnit", filterLoadPostPojo.getmPayloadUnit())
                }
            }
        }

        if (filterLoadPostPojo.getmVehichleLenght() != null) {
            if (filterLoadPostPojo.getmVehichleLenght().isNotEmpty()) {
                baseQuery = baseQuery.whereEqualTo("mVehichleLenght", filterLoadPostPojo.getmVehichleLenght())
                if (filterLoadPostPojo.getmPayloadUnit() != null) {
                    baseQuery = baseQuery.whereEqualTo("mPayloadUnit", filterLoadPostPojo.getmPayloadUnit())
                }
            }
        }

        if (filterLoadPostPojo.getmVehicleType() != null) {
            baseQuery = baseQuery.whereEqualTo("mVehicleType", filterLoadPostPojo.getmVehicleType())
        }

        if (filterLoadPostPojo.getmBodyType() != null) {
            baseQuery = baseQuery.whereEqualTo("mBodyType", filterLoadPostPojo.getmBodyType())
        }


        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<LoadPostPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, LoadPostPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<LoadPostPojo, LoadPostViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): LoadPostViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_load_post, parent, false)
                recyclerViewAnimator.onCreateViewHolder(view)
                return LoadPostViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: LoadPostViewHolder,
                                          position: Int,
                                          @NonNull model: LoadPostPojo) {

                recyclerViewAnimator.onBindViewHolder(holder.itemView,position)
                if (model.getmUid().equals(preferenceManager.userId)) {
                    holder.delete.visibility = View.VISIBLE
                } else {
                    holder.delete.visibility = View.GONE
                }
                holder.authername.text = model.getmDisplayName()
                holder.source.text = model.getmSourceCity()
                holder.destination.text = model.getmDestinationCity()

                if(model.getmVehicleType()!=null){
                    if(model.getmVehicleType().isNotEmpty()){
                        holder.lltype.visibility = View.VISIBLE
                        holder.truck_type.text = model.getmVehicleType()
                    }else{
                        holder.lltype.visibility = View.GONE
                    }
                }else{
                    holder.lltype.visibility = View.GONE
                }


                if(model.getmBodyType()!=null){
                    if(model.getmBodyType().isNotEmpty()){
                        holder.llbody.visibility = View.VISIBLE
                        holder.body_type.text = model.getmBodyType()
                    }else{
                        holder.llbody.visibility = View.GONE
                    }
                }else{
                    holder.lltype.visibility = View.GONE
                }


                if(model.getmPayload()!=null){
                    if(model.getmPayload().isNotEmpty()){
                        holder.llweight.visibility = View.VISIBLE
                        holder.weight.text = model.getmPayload() + " " + model.getmPayloadUnit()

                    }else{
                        holder.llweight.visibility = View.GONE
                    }
                }else{
                    holder.llweight.visibility = View.GONE
                }

                if(model.getmVehichleLenght()!=null){
                    if(model.getmVehichleLenght().isNotEmpty()){
                        holder.lllength.visibility = View.VISIBLE
                        holder.length.text = model.getmVehichleLenght() + " " + model.getmVehichleLenghtUnit()
                    }else{
                        holder.lllength.visibility = View.GONE
                    }
                }else{
                    holder.lllength.visibility = View.GONE
                }

                if(model.getmMaterial()!=null){
                    if(model.getmMaterial().isNotEmpty()){
                        holder.llmaterial.visibility = View.VISIBLE
                        holder.material.text = model.getmMaterial()
                    }else{
                        holder.llmaterial.visibility = View.GONE
                    }
                }else{
                    holder.llmaterial.visibility = View.GONE
                }

                holder.post_requirement.text = model.getmRemark()

                if(model.getmTimeStamp()!=null)
                holder.date.text = SimpleDateFormat("dd MMM / HH:mm").format(model.getmTimeStamp())


                holder.share.setOnClickListener {
                    firebaseAnalytics.logEvent("z_share_clicked_lb", bundle)
                    shareMesssages(context, "Loadpost on ILN", model.toString())
                }

                holder.call.setOnClickListener {

                    val bundle = Bundle()
                    firebaseAnalytics.logEvent("z_call_clicked_lb", bundle)

                    if (model.getmRmn() != null && model.getmRmn().isNotEmpty()) {
                        callNumber(model.getmRmn())
                    } else {
                        Toast.makeText(context, getString(R.string.moble_not_available), Toast.LENGTH_SHORT).show()
                    }
                }

                holder.chat.setOnClickListener {

                    val bundle = Bundle()
                    firebaseAnalytics.logEvent("z_chat_clicked_lb", bundle)

                    val intent = Intent(context, ChatRoomActivity::class.java)
                    intent.putExtra("imsg", model.toString())
                    intent.putExtra("ormn", model.getmRmn())
                    intent.putExtra("ouid", model.getmUid())
                    intent.putExtra("ofuid", model.getmFuid())
                    startActivity(intent)

                }

                holder.delete.setOnClickListener {

                    val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {

                                holder.itemView.visibility = View.GONE

                                FirebaseFirestore.getInstance().collection("loadposts").document(getItem(position)!!.id).delete().addOnCompleteListener {
                                    Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
                                }
                                val bundle = Bundle()
                                firebaseAnalytics.logEvent("z_remove_clicked_lb", bundle)
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                            }
                        }//No button clicked
                    }

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Delete Post")
                    builder.setMessage("Are you sure? You want to delete this post.").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                }

                holder.autherprofile.setOnClickListener {
                    val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                    i.putExtra("uid", model.getmUid())
                    i.putExtra("rmn", model.getmRmn())
                    i.putExtra("fuid", model.getmFuid())
                    startActivity(i)
                }

                if (model.getmPhotoUrl() != null) {
                    if (!model.getmPhotoUrl().isEmpty()) {
                        Picasso.with(holder.itemView.getContext())
                                .load(model.getmPhotoUrl())
                                .placeholder(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
                                .transform(CircleTransform())
                                .fit()
                                .into(holder.thumb, object : Callback {
                                    override fun onSuccess() {
                                        Logger.v("image set: $position")
                                    }

                                    override fun onError() {
                                        Logger.v("image error: " + position + model.getmPhotoUrl())
                                    }

                                })
                    } else {
                        Logger.v("image url empty: $position")
                    }
                } else {
                    Logger.v("image url null: $position")
                }

                holder.loadpostDetails.setOnClickListener {
                    val i = Intent(context, SingleLoadDetailsActivity::class.java)
                    i.putExtra("loadid",getItem(position)!!.id)
                    startActivity(i)
                    Logger.v("load post details: ${getItem(position)!!.id}")

                }


            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslb.visibility = View.VISIBLE
                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslb.visibility = View.VISIBLE
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslb.visibility = View.GONE

                    }

                    LoadingState.FINISHED -> {
                        loadingfslb.visibility = View.GONE
                        if(itemCount==0){
                            noresult.visibility = View.VISIBLE
                        }else{
                            noresult.visibility = View.GONE
                        }
                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        fsloadslist.layoutManager = LinearLayoutManager(context)
        fsloadslist.adapter = adapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOADBOARD_FILTER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                arrangeUIaccordingtofilters()
            }
        }


    }

    private fun shareMesssages(context: Context, subject: String, body: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (exception: ActivityNotFoundException) {
            Toast.makeText(context, "No application found for send Email", Toast.LENGTH_LONG).show()
        }

    }

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(callIntent)
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
