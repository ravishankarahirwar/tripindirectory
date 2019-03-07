package directory.tripin.com.tripindirectory.newlookcode.activities

import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatHeadPojo
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatItemPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.BasicQueryPojo
import directory.tripin.com.tripindirectory.newlookcode.PartnersViewHolder
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.helper.RecyclerViewAnimator
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.models.CompanyCardPojo
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.activity_post_to_selected.*

class PostToSelectedActivity : LocalizationActivity() {

    lateinit var adapter: FirestorePagingAdapter<CompanyCardPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo
    lateinit var context: Context
    lateinit var hashmap: HashMap<String, ChatItemPojo>
    lateinit var msglist: ArrayList<ChatItemPojo>
    var noc: Int = 0
    lateinit var preferenceManager: PreferenceManager
    lateinit var textUtils: TextUtils
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var mAuth : FirebaseAuth
    lateinit var  recyclerViewAnimator: RecyclerViewAnimator




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_to_selected)
        context = this
        textUtils = TextUtils()
        recyclerViewAnimator = RecyclerViewAnimator(rv_transporterss)


        textUtils = TextUtils()
        hashmap = HashMap<String, ChatItemPojo>()
        preferenceManager = PreferenceManager.getInstance(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser == null
                || FirebaseAuth.getInstance().currentUser!!.phoneNumber == null
                || !preferenceManager.isFacebooked) {
            val i = Intent(this@PostToSelectedActivity, FacebookRequiredActivity::class.java)
            i.putExtra("from", "PostToSelected")
            startActivityForResult(i, 3)
            Toast.makeText(applicationContext, getString(R.string.login_fb_required_pts), Toast.LENGTH_LONG).show()
        }



        if (intent.extras != null) {
            if (intent.extras.getSerializable("query") != null) {
                basicQueryPojo = intent.extras.getSerializable("query") as BasicQueryPojo
                if (!basicQueryPojo.mSourceCity.isEmpty() && !basicQueryPojo.mDestinationCity.isEmpty()) {
                    title = "${textUtils.toTitleCase(basicQueryPojo.mSourceCity)} To ${textUtils.toTitleCase(basicQueryPojo.mDestinationCity)}"
                } else {
                    title = "Post To Selected"
                }
                var fleets: String = ""
                for (fleet: String in basicQueryPojo.mFleets!!) {
                    fleets = "$fleets,$fleet"
                }
                if (!fleets.isEmpty()) {
                    supportActionBar!!.subtitle = fleets.substring(1)
                } else {
                    supportActionBar!!.subtitle = fleets
                }
                setMainAdapter(basicQueryPojo)
            }
        }

        sendtoaalfab.setOnClickListener {

            if (noc == 0) {
                Toast.makeText(context, getString(R.string.nothing_selected), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, getString(R.string.sending), Toast.LENGTH_SHORT).show()
                selected_comps.text = getString(R.string.sending_wait)

                //Sending Function

                val list: ArrayList<ChatItemPojo> = ArrayList()
                for (chatItemPojo: ChatItemPojo in hashmap.values) {
                    if (chatItemPojo.selected)
                        list.add(chatItemPojo)
                }
                sendtothefirst(list)
                sendtoaalfab.visibility = View.INVISIBLE

            }


        }

        internetCheck()

        if(!preferenceManager.isPTSScreenGuided){
            showIntro()
        }

    }

    private fun sendtothefirst(values: ArrayList<ChatItemPojo>) {



        if (values.size == 0) {
            //Sending Finished
            Toast.makeText(context, getString(R.string.sending_done), Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            if(preferenceManager.rmn!=null){
                bundle.putString("by_rmn",preferenceManager.rmn)
            }else{
                bundle.putString("by_rmn","Unknown")
            }
            bundle.putString("status","Done")
            firebaseAnalytics.logEvent("z_posted_to_selected", bundle)

            finish()

        } else {
            //#1 Check Chat Room Id Existance
            var chatroomid: String = ""
            FirebaseFirestore.getInstance()
                    .collection("chats")
                    .document("chatheads")
                    .collection(preferenceManager.userId)
                    .document(values[0].getmReciversUid())
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        Logger.v("onSuccess: Checking Chat Heads")
                        if (documentSnapshot.exists()) {
                            val chatHeadPojo = documentSnapshot.toObject(ChatHeadPojo::class.java)!!
                            chatroomid = chatHeadPojo.getmChatRoomId()
                        } else {
                            chatroomid = preferenceManager.userId + values[0].getmReciversUid()
                        }
                        values[0].setmChatRoomId(chatroomid)
                        Logger.v("mChatRoomId in list $0: ${values[0].getmChatRoomId()}")

                        //#2 Send The msg

                        FirebaseFirestore.getInstance().collection("chats").document("chatrooms").collection(chatroomid).add(values[0]).addOnSuccessListener {

                            //#3 Chat Head to the reciever
                            val chatHeadPojo = ChatHeadPojo(chatroomid,
                                    preferenceManager.rmn,
                                    preferenceManager.userId,
                                    preferenceManager.fuid,
                                    basicQueryPojo.toString(),
                                    preferenceManager.imageUrl, preferenceManager.displayName)
                            FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(values[0].getmReciversUid()).document(preferenceManager.userId).set(chatHeadPojo).addOnSuccessListener {


                                //#4 Chat head to the me

                                val chatHeadPojo = ChatHeadPojo(chatroomid,
                                        values[0].getmORMN(),
                                        values[0].getmReciversUid(),
                                        values[0].getmOFUID(),
                                        basicQueryPojo.toString(),
                                        values[0].getmOpponentsImageUrl(), values[0].getmOpponentsDisplayName())

                                FirebaseFirestore.getInstance().collection("chats").document("chatheads").collection(preferenceManager.userId).document(values[0].getmReciversUid()).set(chatHeadPojo).addOnSuccessListener {
                                    Logger.v("heads updated")
                                    //itiration
                                    values.removeAt(0)
                                    //#5 Itirate
                                    sendtothefirst(values)
                                }
                            }
                        }


                    }.addOnFailureListener {
                        Logger.v("onFailure: Checking Chat Heads")
                        Toast.makeText(context, "Sending Failed! Try Again", Toast.LENGTH_SHORT).show()
                        val bundle = Bundle()
                        if(preferenceManager.rmn!=null){
                            bundle.putString("by_rmn",preferenceManager.rmn)
                        }else{
                            bundle.putString("by_rmn","Unknown")
                        }
                        bundle.putString("status","Failed")
                        firebaseAnalytics.logEvent("z_posted_to_selected", bundle)
                        finish()
                    }
        }


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


    private fun setMainAdapter(basicQueryPojo: BasicQueryPojo) {

        val bundle = Bundle()
        Logger.v(basicQueryPojo.toString())

        var source = "ANYWHERE"
        if (!basicQueryPojo.mSourceHub.isEmpty() && basicQueryPojo.mSourceHub != "Select City") {
            bundle.putString("source", basicQueryPojo.mSourceHub)
            source = basicQueryPojo.mSourceHub.toUpperCase()
        } else {
            bundle.putString("source", "Empty")
        }

        var destination = "ANYWHERE"
        if (!basicQueryPojo.mDestinationHub.isEmpty() && basicQueryPojo.mDestinationHub != "Select City") {
            bundle.putString("destination", basicQueryPojo.mDestinationHub)
            destination = basicQueryPojo.mDestinationHub.toUpperCase()
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
        baseQuery = baseQuery.orderBy("mDetails.mProfileType", Query.Direction.DESCENDING)
        baseQuery = baseQuery.whereGreaterThanOrEqualTo("mDetails.mProfileType","1")
        baseQuery = baseQuery.whereEqualTo("mDetails.isSpammed", false)
        baseQuery = baseQuery.whereArrayContains("mDetails.mFleetsSort",fleetssorter)
        baseQuery = baseQuery.orderBy("mBidValue",Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.isActive",Query.Direction.DESCENDING)
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
                        .inflate(R.layout.item_new_select_transporter, parent, false)
                recyclerViewAnimator.onCreateViewHolder(view)
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: CompanyCardPojo) {

                Logger.v("onBind ${position}")


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

                updatebottomview()


                if(model.getmDetails().getmPhotoUrl()!=null){
                    if(!model.getmDetails().getmPhotoUrl().isEmpty()){
                        Picasso.with(applicationContext)
                                .load(model.getmDetails().getmPhotoUrl())
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

                }else{
                    holder.mThumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.emoji_google_category_travel))
                }


                if (model != null) {
                    if (model.getmDetails().getmLocationCity() != null)
                        holder.mAddress.text = model.getmDetails().getmLocationCity()
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
                        holder.mIsPromoted.visibility = View.GONE //Role
                    if (model.getmDetails().getmProfileType() != null) {
                        if (model.getmDetails().getmProfileType().isNotEmpty()) {
                            var type = ""
                            if(model.getmDetails().getmProfileType()=="0"||model.getmDetails().getmProfileType()=="0.5"){
                                type = "LOAD PROVIDER"
                            }
                            if(model.getmDetails().getmProfileType()=="2"||model.getmDetails().getmProfileType()=="2.5"){
                                type = "FLEET PROVIDER"
                            }
                            holder.mRole.text = type
                        }
                    }
                    }
                }

                if (hashmap.contains(model.getmDetails().getmRMN())) { //Role
                    if (model.getmDetails().getmProfileType() != null) {
                        if (model.getmDetails().getmProfileType().isNotEmpty()) {
                            var type = ""
                            if(model.getmDetails().getmProfileType()=="0"||model.getmDetails().getmProfileType()=="0.5"){
                                type = "LOAD PROVIDER"
                            }
                            if(model.getmDetails().getmProfileType()=="2"||model.getmDetails().getmProfileType()=="2.5"){
                                type = "FLEET PROVIDER"
                            }
                            holder.mRole.text = type
                        }
                    }
                    if (hashmap[model.getmDetails().getmRMN()]!!.selected == true) {
                        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_100))
                        holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readred_24dp))
                    } else {
                        holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
                        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

                    }
                } else {
                    holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
                    holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

                }

                holder.mCompany.setOnClickListener {

                    val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                    i.putExtra("uid",getItem(position)!!.id)
                    i.putExtra("rmn",model.getmDetails().getmRMN())
                    i.putExtra("fuid",model.getmDetails().getmFUID())
                    startActivity(i)
                }


                holder.mIsSelectedImg.setOnClickListener { it ->
                    if (hashmap.contains(model.getmDetails().getmRMN())) {
                        Logger.v("Containts....")
                        if (hashmap[model.getmDetails().getmRMN()]!!.selected == true) {
                            val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                    model.getmDetails().getmPhotoUrl(),
                                    getItem(position)!!.id,
                                    preferenceManager.fcmToken,
                                    model.getmDetails().getmFcmToken(),
                                    preferenceManager.rmn,
                                    model.getmDetails().getmRMN(),
                                    model.getmDetails().getmFUID(),
                                    preferenceManager.displayName,
                                    model.getmDetails().getmDisplayName(),
                                    basicQueryPojo.toString(), "",
                                    0, 2)
                            chatItemPojo.selected = false
                            hashmap[model.getmDetails().getmRMN()] = chatItemPojo
                        } else {
                            val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                    model.getmDetails().getmPhotoUrl(),
                                    getItem(position)!!.id,
                                    preferenceManager.fcmToken,
                                    model.getmDetails().getmFcmToken(),
                                    preferenceManager.rmn,
                                    model.getmDetails().getmRMN(),
                                    model.getmDetails().getmFUID(),
                                    preferenceManager.displayName,
                                    model.getmDetails().getmDisplayName(),
                                    basicQueryPojo.toString(), "",
                                    0, 2)
                            chatItemPojo.selected = true
                            hashmap[model.getmDetails().getmRMN()] = chatItemPojo
                        }
                        Logger.v("selected: ${hashmap[model.getmDetails().getmRMN()]!!.selected}")
                    } else {

                        val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                model.getmDetails().getmPhotoUrl(),
                                getItem(position)!!.id,
                                preferenceManager.fcmToken,
                                model.getmDetails().getmFcmToken(),
                                preferenceManager.rmn,
                                model.getmDetails().getmRMN(),
                                model.getmDetails().getmFUID(),
                                preferenceManager.displayName,
                                model.getmDetails().getmDisplayName(),
                                basicQueryPojo.toString(), "",
                                0, 2)
                        chatItemPojo.selected = true
                        hashmap[model.getmDetails().getmRMN()] = chatItemPojo
                    }

                    notifyItemChanged(position)

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
                        loadingpts.visibility = View.VISIBLE

                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingpts.visibility = View.GONE

                    }

                    LoadingState.FINISHED -> {
                        loadingpts.visibility = View.GONE
                        Logger.v("onLoadingStateChanged ${state.name}")

                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }



        rv_transporterss.layoutManager = LinearLayoutManager(this)
        rv_transporterss.adapter = adapter


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (requestCode == 3) {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun updatebottomview() {
        noc = 0
        for (rmn: String in hashmap.keys) {
            if (hashmap[rmn]!!.selected == true) {
                noc += 1
                Logger.v("noc++ $noc")

            }
        }
        if (noc == 0) {
            selected_comps.text = "No Companies are selected"
            selected_comps.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_100))
        } else {
            if (noc == 1) {
                selected_comps.text = "$noc Company is selected"
                selected_comps.setBackgroundColor(ContextCompat.getColor(context, R.color.green_300))
            } else {
                selected_comps.text = "$noc Companies are selected"
                selected_comps.setBackgroundColor(ContextCompat.getColor(context, R.color.green_300))
            }
        }
        Logger.v("updatedbottomView $noc")

    }

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(callIntent)
    }

    private fun showIntro() {

        val tapTargetSequence : TapTargetSequence = TapTargetSequence(this)
                .targets(
                        TapTarget.forView(sendtoaalfab, "Send to Selected feature","Select the transporters from the list and click on send. Your requirement details will be sent to all in the chat. You can proceed your transaction further with interested members. Tap on the target!")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(false).outerCircleColor(R.color.primaryColor)

                )
                .listener(object: TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                    override fun onSequenceFinish() {
                        Toast.makeText(applicationContext,"Use ILN wisely!",Toast.LENGTH_SHORT).show()
                        preferenceManager.setisPTSScreenGuided(true)
                    }
                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // Boo
                        preferenceManager.setisPTSScreenGuided(true)
                    }
                })

        tapTargetSequence.start()
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
