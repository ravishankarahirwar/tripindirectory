package directory.tripin.com.tripindirectory.NewLookCode.activities

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
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatHeadPojo
import directory.tripin.com.tripindirectory.ChatingActivities.models.ChatItemPojo
import directory.tripin.com.tripindirectory.NewLookCode.BasicQueryPojo
import directory.tripin.com.tripindirectory.NewLookCode.PartnersViewHolder
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.activity_post_to_selected.*
import kotlinx.android.synthetic.main.content_main_scrolling.*

class PostToSelectedActivity : AppCompatActivity() {

    lateinit var adapter: FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo
    lateinit var context: Context
    lateinit var hashmap: HashMap<String, ChatItemPojo>
    lateinit var msglist: ArrayList<ChatItemPojo>
    var noc: Int = 0
    lateinit var preferenceManager: PreferenceManager
    lateinit var textUtils: TextUtils



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_to_selected)
        context = this
        textUtils = TextUtils()

        textUtils = TextUtils()
        hashmap = HashMap<String, ChatItemPojo>()
        preferenceManager = PreferenceManager.getInstance(context)


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
                Toast.makeText(context, "Nothing Selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Sending", Toast.LENGTH_SHORT).show()

                //Sending Function

                val list: ArrayList<ChatItemPojo> = ArrayList()
                for (chatItemPojo: ChatItemPojo in hashmap.values) {
                    if (chatItemPojo.selected)
                        list.add(chatItemPojo)
                }
                sendtothefirst(list)


            }


        }

    }

    private fun sendtothefirst(values: ArrayList<ChatItemPojo>) {



        if (values.size == 0) {
            //Sending Finished
            Toast.makeText(context, "Sending Done!", Toast.LENGTH_SHORT).show()
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

        Logger.v(basicQueryPojo.toString())

        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("partners")

        if (!basicQueryPojo.mSourceCity.isEmpty() && basicQueryPojo.mSourceCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mSourceHubs.${basicQueryPojo.mSourceCity.toUpperCase()}", true)

        }
        if (!basicQueryPojo.mDestinationCity.isEmpty() && basicQueryPojo.mDestinationCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mDestinationHubs.${basicQueryPojo.mDestinationCity.toUpperCase()}", true)

        }

        for (fleet in basicQueryPojo.mFleets!!) {
            baseQuery = baseQuery.whereEqualTo("fleetVehicle.$fleet", true)
        }
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<PartnerInfoPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, PartnerInfoPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {

                val view: View = if (viewType == 1) {
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_transporter_loading, parent, false)
                } else {
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_new_select_transporter, parent, false)
                }
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: PartnerInfoPojo) {
                holder.mCompany.text = textUtils.toTitleCase(model.getmCompanyName())
                updatebottomview()

                if(model.getmPhotoUrl()!=null){
                    if(!model.getmPhotoUrl().isEmpty()){
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

                }else{
                    holder.mThumbnail.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.emoji_google_category_travel))
                }


                if (model != null) {
                    if (model.getmCompanyAdderss() != null)
                        holder.mAddress.text = model.getmCompanyAdderss().city
                }

                if (hashmap.contains(model.getmRMN())) {
                    if (hashmap[model.getmRMN()]!!.selected == true) {
                        holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readred_24dp))
                        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue_grey_100))
                    } else {
                        holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
                        holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

                    }
                } else {
                    holder.mIsSelectedImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_readgrey_24dp))
                    holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))

                }

                holder.mCompany.setOnClickListener {

                    val i = Intent(context, PartnerDetailScrollingActivity::class.java)
                    i.putExtra("uid", getItem(position)!!.id)
                    i.putExtra("cname", model.getmCompanyName())
                    startActivity(i)
                }

                holder.mIsSelectedImg.setOnClickListener { it ->
                    if (hashmap.contains(model.getmRMN())) {
                        Logger.v("Containts....")
                        if (hashmap[model.getmRMN()]!!.selected == true) {
                            val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                    model.getmPhotoUrl(),
                                    getItem(position)!!.id,
                                    preferenceManager.fcmToken,
                                    model.getmFcmToken(),
                                    preferenceManager.rmn,
                                    model.getmRMN(),
                                    model.getmFUID(),
                                    preferenceManager.displayName,
                                    model.getmDisplayName(),
                                    basicQueryPojo.toString(), "",
                                    0, 2)
                            chatItemPojo.selected = false
                            hashmap[model.getmRMN()] = chatItemPojo
                        } else {
                            val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                    model.getmPhotoUrl(),
                                    getItem(position)!!.id,
                                    preferenceManager.fcmToken,
                                    model.getmFcmToken(),
                                    preferenceManager.rmn,
                                    model.getmRMN(),
                                    model.getmFUID(),
                                    preferenceManager.displayName,
                                    model.getmDisplayName(),
                                    basicQueryPojo.toString(), "",
                                    0, 2)
                            chatItemPojo.selected = true
                            hashmap[model.getmRMN()] = chatItemPojo
                        }
                        Logger.v("selected: ${hashmap[model.getmRMN()]!!.selected}")
                    } else {

                        val chatItemPojo = ChatItemPojo(preferenceManager.userId, preferenceManager.fuid, preferenceManager.imageUrl,
                                model.getmPhotoUrl(),
                                getItem(position)!!.id,
                                preferenceManager.fcmToken,
                                model.getmFcmToken(),
                                preferenceManager.rmn,
                                model.getmRMN(),
                                model.getmFUID(),
                                preferenceManager.displayName,
                                model.getmDisplayName(),
                                basicQueryPojo.toString(), "",
                                0, 2)
                        chatItemPojo.selected = true
                        hashmap[model.getmRMN()] = chatItemPojo
                    }

                    notifyItemChanged(position)

                }

                holder.mCall.setOnClickListener {


                    val phoneNumbers = java.util.ArrayList<String>()
                    val contactPersonPojos = model.getmContactPersonsList()

                    if (contactPersonPojos != null && contactPersonPojos.size > 1) {
                        for (i in contactPersonPojos.indices) {
                            if (model.getmContactPersonsList()[i] != null) {
                                val number = model.getmContactPersonsList()[i].getmContactPersonMobile
                                phoneNumbers.add(number)

                            }
                        }

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Looks like there are multiple phone numbers.")
                                .setCancelable(false)
                                .setAdapter(ArrayAdapter(context, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers)
                                ) { dialog, item ->
                                    Logger.v("Dialog number selected :" + phoneNumbers[item])

                                    callNumber(phoneNumbers[item])
                                }

                        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                // User cancelled the dialog
                            }
                        })
                        builder.create()
                        builder.show()


                    } else {

                        val number = model.getmContactPersonsList()[0].getmContactPersonMobile
                        callNumber(number)
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
}
