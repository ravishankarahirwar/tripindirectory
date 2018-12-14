package directory.tripin.com.tripindirectory.newprofiles.activities

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newlookcode.PartnersViewHolder
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newprofiles.models.ConnectPojo
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.activity_all_transporters.*
import kotlinx.android.synthetic.main.activity_my_network.*
import kotlinx.android.synthetic.main.content_main_scrolling.*
import java.text.SimpleDateFormat
import java.util.*


class MyNetworkActivity : LocalizationActivity() {

    lateinit var adapter: FirestorePagingAdapter<ConnectPojo, PartnersViewHolder>
    lateinit var context: Context
    lateinit var textUtils: TextUtils
    lateinit var preferenceManager: PreferenceManager
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var  recyclerViewAnimator: RecyclerViewAnimator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_network)
        context = this
        textUtils = TextUtils()
        preferenceManager = PreferenceManager.getInstance(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        recyclerViewAnimator = RecyclerViewAnimator(rv_mynetwork)


        back_mynetwork.setOnClickListener {
            finish()
        }
        setAdapter()
    }



    private fun setAdapter() {
        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("networks").document(preferenceManager.userId).collection("mNetwork").whereEqualTo("mStatus",true)
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<ConnectPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, ConnectPojo::class.java)
                .build()

        adapter = object : FirestorePagingAdapter<ConnectPojo, PartnersViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {

                val view: View =
                        LayoutInflater.from(parent.context)
                                .inflate(R.layout.item_new_transporter, parent, false)

                recyclerViewAnimator.onCreateViewHolder(view)


                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: ConnectPojo) {
                if (model != null) {

                    recyclerViewAnimator.onBindViewHolder(holder.itemView,position)

                    holder.mIsPromoted.visibility = View.GONE
                    holder.mReviews.visibility = View.GONE
                    holder.mRatings.visibility = View.GONE
                    holder.mOnlineStatus.visibility = View.GONE

                    if (model.getmCompanyName() != null) {
                        if (!model.getmCompanyName().isEmpty()) {
                            holder.mCompany.text = textUtils.toTitleCase(model.getmCompanyName())
                        } else {
                            holder.mCompany.text = "Unknown Name"
                            if(model.getmDisplayName() != null){
                                if(!model.getmDisplayName().isEmpty()){
                                    holder.mCompany.text = model.getmDisplayName()
                                }
                            }
                        }
                    } else {
                        holder.mCompany.text = "Unknown Name"
                        if(model.getmDisplayName() != null){
                            if(!model.getmDisplayName().isEmpty()){
                                holder.mCompany.text = model.getmDisplayName()
                            }
                        }
                    }


                    if (model.getmRmn() != null) {
                        holder.mAddress.text = model.getmRmn()
                    }


                    if (model.getmPhotoUrl() != null) {
                        if (!model.getmPhotoUrl().isEmpty()) {
                            Picasso.with(applicationContext)
                                    .load(model.getmPhotoUrl())
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



                    holder.itemView.setOnClickListener {

                        val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                        i.putExtra("uid", model.getmUid())
                        i.putExtra("rmn", model.getmRmn())
                        i.putExtra("fuid", model.getmFuid())
                        startActivity(i)
                    }

                    holder.mCall.setOnClickListener {
                        val interactionPojo = InteractionPojo(preferenceManager.userId,
                                preferenceManager.fuid,
                                preferenceManager.rmn,
                                preferenceManager.comapanyName, preferenceManager.displayName,
                                preferenceManager.fcmToken,
                                model.getmUid(),
                                model.getmFuid(),model.getmRmn(),model.getmCompanyName(),model.getmDisplayName(),"")

                        FirebaseFirestore.getInstance().collection("partners")
                                .document(model.getmUid()).collection("mCallsDump").document(getDateString())
                                .collection("interactors").document(preferenceManager.userId).set(interactionPojo)

                        FirebaseFirestore.getInstance().collection("partners")
                                .document(model.getmUid())
                                .collection("mCalls").document(preferenceManager.userId).set(interactionPojo).addOnCompleteListener {
                                    FirebaseFirestore.getInstance().collection("partners")
                                            .document(preferenceManager.userId)
                                            .collection("mCalls").document(model.getmUid()).set(interactionPojo)
                                }

                        if (model.getmRmn() != null) {
                            callNumber(model.getmRmn())
                        }
                    }

                    holder.mChat.setOnClickListener {

                        val intent = Intent(context, ChatRoomActivity::class.java)
                        intent.putExtra("imsg", "From MyNetwork")
                        intent.putExtra("ormn", model.getmRmn())
                        intent.putExtra("ouid", getItem(position)!!.id)
                        intent.putExtra("ofuid", model.getmFuid())
                        startActivity(intent)

                    }
                }


            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingmn.visibility = View.VISIBLE

                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingmn.visibility = View.VISIBLE
                        if (itemCount != 0) {
                            loadingmn.visibility = View.GONE
                        }
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name} $itemCount")
                        loadingmn.visibility = View.GONE
                        if (itemCount != 0) {
                            networkemptyinfo.visibility = View.GONE
                        }else{
                            networkemptyinfo.visibility = View.VISIBLE
                        }

                    }

                    LoadingState.FINISHED -> {
                        Logger.v("onLoadingStateChanged ${state.name} $itemCount")
                        loadingmn.visibility = View.GONE
                        if (itemCount != 0) {
                            networkemptyinfo.visibility = View.GONE
                        }else{
                            networkemptyinfo.visibility = View.VISIBLE
                        }

                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        rv_mynetwork.layoutManager = LinearLayoutManager(this)
        rv_mynetwork.adapter = adapter    }

    private fun callNumber(number: String) {

        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(callIntent)
    }

    private fun getDateString(): String {
        return SimpleDateFormat("dd-MM-yyyy").format(Date())
    }


}
