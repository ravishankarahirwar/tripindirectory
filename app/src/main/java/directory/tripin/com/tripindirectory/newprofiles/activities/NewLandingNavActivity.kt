package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatHeadsActivity
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.chatingactivities.models.ChatIndicatorPojo
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.activities.*
import directory.tripin.com.tripindirectory.newlookcode.viewholders.RecentSearchesViewHolder
import directory.tripin.com.tripindirectory.newprofiles.CompanyRatingsPojo
import directory.tripin.com.tripindirectory.newprofiles.models.DirectorySearchPojo
import directory.tripin.com.tripindirectory.utils.AppUtils
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_main_scrolling.*
import kotlinx.android.synthetic.main.activity_new_landing_nav.*
import kotlinx.android.synthetic.main.layout_choose_vehicle.*
import kotlinx.android.synthetic.main.layout_main_actionbar.*
import kotlinx.android.synthetic.main.layout_route_input.*
import java.math.RoundingMode

class NewLandingNavActivity : AppCompatActivity() {

    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var context : Context
    lateinit var preferenceManager : PreferenceManager
    var pendingChatsvalueEventListener: ValueEventListener? = null
    var mMyIndicator: ChatIndicatorPojo? = null
    lateinit var appUtils: AppUtils





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_landing_nav)
        context = this
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        preferenceManager = PreferenceManager.getInstance(context)
        appUtils = AppUtils(context)
        setListners()

        if (!preferenceManager.isMainScreenGuided) {
            showIntro()
        }else{
            if(preferenceManager.settingPopupinvite){
                showInvitesScreen()
            }
        }

        internetCheck()


    }

    override fun onResume() {
        super.onResume()
        if (preferenceManager.userId != null) {
            setChatPendingIndicator()
            setRecentSearchesAdapter()
        }
    }

    override fun onStop() {
        if (pendingChatsvalueEventListener != null) {
            if (preferenceManager.userId != null)
                FirebaseDatabase.getInstance().reference.child("chatpresence").child("chatpendings").child(preferenceManager.userId).removeEventListener(pendingChatsvalueEventListener!!)
        }
        super.onStop()
    }

    private fun setListners() {

        gotodirectory.setOnClickListener {
            val i = Intent(this, MainScrollingActivity::class.java)
            startActivity(i)
        }

        searchbynamegoto.setOnClickListener {
            startSearchCompanyActivity()
        }
        gotoloadboard.setOnClickListener {
            startLoadboard()
        }

        yourbusiness.setOnClickListener {
            startYourBusinessActivity("ActionBar")
        }
        showchats.setOnClickListener {
            setChatHeadsActivity("ActionBar")
        }

        mainhumb.setOnClickListener {
            startInfoActivity()
        }

        businessprofile.setOnClickListener {
            startBusinessProfile()
        }

        showcalls.setOnClickListener {
            showRecentCalls()
        }

        directloadpost.setOnClickListener {
            startNewLoadpost()
        }



        fabMenu.bindAnchorView(fab)
        fabMenu.setOnFABMenuSelectedListener { view, id ->
            when (id) {
                R.id.action_loadboard -> {
                    startLoadboard()
                }
                R.id.action_business -> {
                    startYourBusinessActivity("BottomMenu")
                }
                R.id.action_chat -> {
                    setChatHeadsActivity("BottomMenu")
                }
                R.id.action_rate -> {
                    rateApp()
                }
                R.id.action_guide -> {
                    showIntro()
                }
                R.id.action_invite -> {
                    invite()
                }
                R.id.action_about -> {
                }

                R.id.action_feedback -> {
                    chatwithassistant()
                }
//                R.id.action_logout -> {
//                    AuthUI.getInstance().signOut(context).addOnSuccessListener {
//                        Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
//                        preferenceManager.setisFacebboked(false)
//                        preferenceManager.rmn = null
//                        val i = Intent(this, NewSplashActivity::class.java)
//                        startActivity(i)
//                        finish()
//                        val bundle = Bundle()
//                        firebaseAnalytics.logEvent("z_logout", bundle)
//                    }
//
//                }
            }
        }

    }

    private fun startNewLoadpost() {
        val i = Intent(this, NewLoadFormActivity::class.java)
        startActivity(i)
    }

    private fun showRecentCalls() {
        val i = Intent(this, RecentCallsActivity::class.java)
        startActivity(i)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_recent_calls", bundle)
    }

    private fun startBusinessProfile() {
        val i = Intent(this, CompanyProfileDisplayActivity::class.java)
        i.putExtra("uid",preferenceManager.userId)
        i.putExtra("rmn",preferenceManager.rmn)
        i.putExtra("fuid",preferenceManager.fuid)
        startActivity(i)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_my_profile", bundle)    }

    private fun showIntro() {

        val tapTargetSequence: TapTargetSequence = TapTargetSequence(this)
                .targets(
                        TapTarget.forView(mainhumb, "Welcome to ILN Directory", "Lets get through what you can explore here. Tap on the target!")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(false).outerCircleColor(R.color.primaryColor),

                        TapTarget.forView(yourbusiness, "Your Profile Here", "See your Network, Wallet and Add your own business on ILN. For Free!")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor),
                        TapTarget.forView(showchats, "Your Chats Here", "All your chats are saved here, Now keep your business talks at one place. Tap the target!")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor)

                )
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                    override fun onSequenceFinish() {
                        Toast.makeText(applicationContext, "Use ILN wisely!", Toast.LENGTH_SHORT).show()
                        preferenceManager.setisMainScreenGuided(true)
                        val bundle = Bundle()
                        firebaseAnalytics.logEvent("z_mainguidefifished", bundle)
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // Boo
                        preferenceManager.setisMainScreenGuided(true)
                        Toast.makeText(applicationContext, "You can restart the guide from menu!", Toast.LENGTH_SHORT).show()
                    }
                })

        tapTargetSequence.start()
    }

    private fun showInvitesScreen() {
        val i = Intent(this, InvitePhonebookActivity::class.java)
        startActivity(i)
    }


    private fun startLoadboard() {
        val i = Intent(this, FSLoadBoardActivity::class.java)
        startActivity(i)    }

    private fun startSearchCompanyActivity() {
        val i = Intent(this, SearchCompanyActivity::class.java)
        startActivity(i)
    }

    private fun startYourBusinessActivity(s: String) {


        val i = Intent(this, UserEditProfileActivity::class.java)
        startActivity(i)

    }

    private fun setChatHeadsActivity(s: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            // already signed in
            val i = Intent(this, ChatHeadsActivity::class.java)
            startActivity(i)
            val bundle = Bundle()
            bundle.putString("from", s)
            firebaseAnalytics.logEvent("z_openchatheads", bundle)

        } else {
            // not signed in
            finish()
        }

    }

    private fun invite() {
        val params = Bundle()
        firebaseAnalytics.logEvent("z_share_clicked", params)
        appUtils.shareApp()
        //startActivityForResult(appUtils.getInviteIntent(), REQUEST_INVITE)
    }


    internal fun rateApp() {
        val uri = Uri.parse("market://details?id=" + context.getPackageName())
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())))
        }

    }

    private fun startInfoActivity() {
        val i = Intent(this, AboutIlnActivity::class.java)
        startActivity(i)
    }

    private fun chatwithassistant() {
        val intent = Intent(this@NewLandingNavActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)
    }

    private fun setChatPendingIndicator() {
        pendingChatsvalueEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                chatloading.visibility = View.GONE
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatloading.visibility = View.GONE
                if (p0.exists()) {
                    mMyIndicator = p0.getValue(ChatIndicatorPojo::class.java)
                    if (mMyIndicator != null) {
                        if (mMyIndicator!!.getmMsgCount() > 0) {
                            chatbadge.visibility = View.VISIBLE

                            if (mMyIndicator!!.getmLastMsgUserImageUrl() != null) {
                                if (!mMyIndicator!!.getmLastMsgUserImageUrl().isEmpty()) {
                                    lastmsgimgurl.visibility = View.VISIBLE
                                    val url = mMyIndicator!!.getmLastMsgUserImageUrl()
                                    Picasso.with(applicationContext)
                                            .load(url)
                                            .placeholder(ContextCompat.getDrawable(applicationContext, R.drawable.emoji_google_1f464))
                                            .transform(CircleTransform())
                                            .fit()
                                            .into(lastmsgimgurl, object : Callback {

                                                override fun onSuccess() {
                                                }

                                                override fun onError() {
                                                    lastmsgimgurl.visibility = View.INVISIBLE
                                                }
                                            })
                                }

                            }
                        } else {
                            chatbadge.visibility = View.INVISIBLE
                            lastmsgimgurl.visibility = View.INVISIBLE
                        }
                    } else {
                        chatbadge.visibility = View.INVISIBLE
                        lastmsgimgurl.visibility = View.INVISIBLE
                    }
                } else {
                    chatbadge.visibility = View.INVISIBLE
                    lastmsgimgurl.visibility = View.INVISIBLE
                }
            }

        }
        FirebaseDatabase.getInstance().reference.child("chatpresence").child("chatpendings").child(preferenceManager.userId).addValueEventListener(pendingChatsvalueEventListener as ValueEventListener)

    }

    private fun setRecentSearchesAdapter() {

        val query = FirebaseFirestore.getInstance()
                .collection("partners").document(preferenceManager.userId).collection("mDirectoryUsage")
                .orderBy("mTimeStamp",Query.Direction.DESCENDING).limit(5)

        val options = FirestoreRecyclerOptions.Builder<DirectorySearchPojo>()
                .setQuery(query, DirectorySearchPojo::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter: FirestoreRecyclerAdapter<DirectorySearchPojo, RecentSearchesViewHolder>
        adapter = object : FirestoreRecyclerAdapter<DirectorySearchPojo, RecentSearchesViewHolder>(options) {

            override fun onBindViewHolder(holder: RecentSearchesViewHolder, position: Int, model: DirectorySearchPojo) {

                holder.fromCity.text = model.getmSourceCity()
                holder.toCity.text = model.getmDestinationCity()

                holder.itemView.setOnClickListener {
                    val i = Intent(context, MainScrollingActivity::class.java)
                    i.putExtra("query", model)
                    startActivity(i)
                }

            }


            override fun onCreateViewHolder(group: ViewGroup, i: Int): RecentSearchesViewHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view = LayoutInflater.from(group.context)
                        .inflate(R.layout.item_recent_directorysearch, group, false)
                return RecentSearchesViewHolder(view)
            }

            override fun onDataChanged() {
                super.onDataChanged()
                Logger.v("setRecentSearchesAdapter $itemCount")

            }
        }

        recent_searches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recent_searches.adapter = adapter

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
