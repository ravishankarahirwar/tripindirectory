package directory.tripin.com.tripindirectory.newlookcode.activities

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
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.viewholders.LoadPostViewHolder
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import kotlinx.android.synthetic.main.activity_fsload_board.*
import kotlinx.android.synthetic.main.activity_manage_loads.*
import kotlinx.android.synthetic.main.layout_fsyourloads_actionbar.*
import java.text.SimpleDateFormat

class ManageLoadsActivity : LocalizationActivity(){

    lateinit var adapter: FirestorePagingAdapter<LoadPostPojo, LoadPostViewHolder>
    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var recyclerViewAnimator: RecyclerViewAnimator





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_loads)
        context = this
        recyclerViewAnimator = RecyclerViewAnimator(yourloadslist)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        preferenceManager = PreferenceManager.getInstance(context)
        setListners()

    }

    override fun onResume() {
        super.onResume()
        setAdapter()

    }

    private fun setListners() {

        backfslby.setOnClickListener {
            finish()
        }
        addnewload.setOnClickListener {
            val i = Intent(this, NewLoadFormActivity::class.java)
            startActivity(i)
        }

        fabsyncmyload.setOnClickListener {
            setAdapter()
        }

        swiperefreshmyloads.setOnRefreshListener {
            swiperefreshmyloads.isRefreshing = false
            setAdapter()
        }
    }

    private fun setAdapter() {



        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("loadposts")

        //time sort
        baseQuery = baseQuery.orderBy("mTimeStamp", Query.Direction.DESCENDING)
        baseQuery = baseQuery.whereEqualTo("mUid",preferenceManager.userId)

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
                    holder.llbody.visibility = View.GONE
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
                    val bundle = Bundle()
                    firebaseAnalytics.logEvent("z_share_clicked_lb", bundle)
                    shareMesssages(context, "Loadpost on ILN", model.toString())
                }

                holder.call.setOnClickListener {

                    val bundle = Bundle()
                    firebaseAnalytics.logEvent("z_call_clicked_lb", bundle)

                    if (model.getmRmn() != null && model.getmRmn().isNotEmpty()) {
                        callNumber(model.getmRmn())
                    } else {
                        Toast.makeText(context, "Sorry!! Mobile no not available", Toast.LENGTH_SHORT).show()
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

                                FirebaseFirestore.getInstance().collection("loadposts").document(getItem(position)!!.id).delete().addOnCompleteListener {
                                    Toast.makeText(context, getString(R.string.removed_successfully), Toast.LENGTH_SHORT).show()
                                }

                                holder.itemView.visibility = View.GONE
                                val bundle = Bundle()
                                firebaseAnalytics.logEvent("z_remove_clicked_lb", bundle)
                            }

                            DialogInterface.BUTTON_NEGATIVE -> {
                            }
                        }//No button clicked
                    }

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(getString(R.string.delete_post))
                    builder.setMessage(getString(R.string.want_to_delete_post)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                            .setNegativeButton(getString(R.string.no), dialogClickListener).show()
                }

                holder.autherprofile.setOnClickListener {
                    val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                    i.putExtra("uid", getItem(position)!!.id)
                    i.putExtra("rmn", model.getmRmn())
                    i.putExtra("fuid", model.getmFuid())
                    startActivity(i)
                }

                holder.loadpostDetails.setOnClickListener {
                    val i = Intent(context, SingleLoadDetailsActivity::class.java)
                    i.putExtra("loadid",getItem(position)!!.id)
                    startActivity(i)
                    Logger.v("load post details: ${getItem(position)!!.id}")

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


            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.VISIBLE
                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.VISIBLE
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.GONE

                    }

                    LoadingState.FINISHED ->{
                        loadingfslby.visibility = View.GONE
                        if(itemCount==0){
                            noyourloads.visibility = View.VISIBLE
                        }else{
                            noyourloads.visibility = View.GONE
                        }
                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        yourloadslist.layoutManager = LinearLayoutManager(this)
        yourloadslist.adapter = adapter


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
}
