package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Html
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_main_profile_insight.*
import directory.tripin.com.tripindirectory.Messaging.Class.Chat
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.QuerySnapshot
import com.jaredrummler.materialspinner.MaterialSpinner
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.viewholders.RecentCallsViewHolder
import kotlinx.android.synthetic.main.activity_company_profile_display.*
import kotlinx.android.synthetic.main.activity_user_edit_profile.*


class MainProfileInsightActivity : AppCompatActivity() {

lateinit var preferenceManager: PreferenceManager
    lateinit var context : Context
    lateinit var mCompUid : String
    var mCompName : String = "Name"
    var mCompRMN : String = "***"
    lateinit var mCompPhotourl : String
    var mDays : Long = 7



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile_insight)
        setListners()
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        getIntentData()


    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.extras.getString("uid") != null){
                mCompUid = intent.extras.getString("uid")
            }
            if (intent.extras.getString("name") != null){
                mCompName = intent.extras.getString("name")
            }
            if (intent.extras.getString("rmn") != null){
                mCompRMN = intent.extras.getString("rmn")
            }
            if (intent.extras.getString("photourl") != null){
                mCompPhotourl = intent.extras.getString("photourl")
            }
        }
        if(mCompUid==null){
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        setSpinner()
        setupView()
    }

    private fun setupView() {
        setUpImage()
        setRMN()
        FetchAnalytics(mCompUid)
    }



    private fun setRMN() {
        rmn_insight.text = mCompRMN
        compname_insight.text = mCompName
    }

    private fun setSpinner() {
        val spinnerTime = findViewById<MaterialSpinner>(R.id.spinneranalyticsdate)
        spinnerTime.setItems("Last 7 days",
                "Last 30 days")
        spinnerTime.setOnItemSelectedListener { view, position, id, item ->
            // handle click
            when(position){
                0 -> {
                    mDays = 7
                    setupView()
                }
                1 -> {
                    mDays = 30
                    setupView()
                }
            }
        }
    }

    private fun setUpImage() {
        if (mCompPhotourl != null) {
            if (!mCompPhotourl.isEmpty()) {
                Picasso.with(applicationContext)
                        .load(mCompPhotourl + "?width=160&width=160")
                        .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .into(userimage_insight, object : Callback {

                            override fun onSuccess() {
                                Logger.v("image set: profile thumb")
                                Logger.v(mCompPhotourl)
                            }

                            override fun onError() {
                                Logger.v("image profile Error")
                            }
                        })
            }

        } else {
            userimage.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
        }
    }


    private fun setListners() {

        back_maininsight.setOnClickListener {
            finish()
        }

    }

    private fun FetchAnalytics(userId: String) {
        fetchVisits(userId)
        fetchCalls(userId)
        fetchChats(userId)
    }

    private fun fetchChats(userId: String) {
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(userId)
                .collection("mChatsDump").limit(mDays)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {
                                val count: Long = it.getLong("mNumDocs")!!
                                visits += count
                            }
                        }
                        chats_count.text = " $visits"

                    } else {

                        chats_count.text = " New"



                    }
                })
    }

    private fun fetchCalls(userId: String) {
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(userId)
                .collection("mCallsDump").limit(mDays)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {
                                val count: Long = it.getLong("mNumDocs")!!
                                visits += count
                            }
                        }
                        calls_count.text = " $visits"

                    } else {

                        calls_count.text = " New"

                    }
                })

    }

    private fun fetchVisits(userId: String) {
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(userId)
                .collection("mProfileVisits").limit(mDays)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {
                                val count: Long = it.getLong("mNumVisits")!!
                                visits += count
                            }
                        }
                        visits_count.text = " $visits"


                    } else {

                        visits_count.text = " New"

                    }
                })
    }
}
