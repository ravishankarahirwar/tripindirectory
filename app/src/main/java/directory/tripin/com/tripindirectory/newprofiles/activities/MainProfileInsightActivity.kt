package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_main_profile_insight.*
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.jaredrummler.materialspinner.MaterialSpinner
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import kotlinx.android.synthetic.main.activity_user_edit_profile.*
import java.text.SimpleDateFormat
import java.util.*


class MainProfileInsightActivity : LocalizationActivity() {

    /**
     * MainProfileInsightActivity shows traction analytics
     * of the copmany profile
     * @author shubhamsardar
     */

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var mCompUid: String
    var mCompName: String = "Name"
    var mCompRMN: String = "***"
    lateinit var mCompPhotourl: String
    var mDays: Long = 7


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_profile_insight)
        setListners()
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        getIntentData()
        internetCheck()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.extras.getString("uid") != null) {
                mCompUid = intent.extras.getString("uid")
            }
            if (intent.extras.getString("name") != null) {
                mCompName = intent.extras.getString("name")
            }
            if (intent.extras.getString("rmn") != null) {
                mCompRMN = intent.extras.getString("rmn")
            }
            if (intent.extras.getString("photourl") != null) {
                mCompPhotourl = intent.extras.getString("photourl")
            }
        }
        if (mCompUid == null) {
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
        spinnerTime.setItems("Last 7 active days",
                "Last 30 active days")
        spinnerTime.setOnItemSelectedListener { view, position, id, item ->
            // handle click
            when (position) {
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
                .collection("mChatsDump").orderBy("mDate", Query.Direction.DESCENDING).limit(mDays)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0
                        var c: Long = 0
                        var cvisits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {

                                if (c < mDays) {
                                    val count: Long = it.getLong("mNumDocs")!!
                                    visits += count
                                } else {
                                    val count: Long = it.getLong("mNumDocs")!!
                                    cvisits += count
                                }
                                c++
                            }
                        }
                        chats_count.text = " $visits"
                        if ((visits - cvisits > 0)) {
                            chats_comparison.text = "+${visits - cvisits} Vs ${getComparisonDateString()}"
                        } else {
                            chats_comparison.text = "${visits - cvisits} Vs ${getComparisonDateString()}"
                        }

                    } else {

                        chats_count.text = " New"

                    }
                })
    }

    private fun fetchCalls(userId: String) {
        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(userId)
                .collection("mCallsDump").orderBy("mDate", Query.Direction.DESCENDING).limit(mDays)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {
                        var visits: Long = 0
                        var c: Long = 0
                        var cvisits: Long = 0
                        snapshot.forEach {
                            if (it.id.length == 10) {

                                if (c < mDays) {
                                    val count: Long = it.getLong("mNumDocs")!!
                                    visits += count
                                } else {
                                    val count: Long = it.getLong("mNumDocs")!!
                                    cvisits += count
                                }
                                c++
                            }
                        }
                        calls_count.text = " $visits"
                        if ((visits - cvisits > 0)) {
                            calls_comparison.text = "+${visits - cvisits} Vs ${getComparisonDateString()}"
                        } else {
                            calls_comparison.text = "${visits - cvisits} Vs ${getComparisonDateString()}"
                        }

                    } else {
                        calls_count.text = " New"
                    }
                })

    }

    private fun fetchVisits(userId: String) {

        FirebaseFirestore.getInstance()
                .collection("partners")
                .document(userId)
                .collection("mProfileVisits").orderBy("mDate", Query.Direction.DESCENDING).limit(mDays * 2)
                .addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }

                    if (snapshot != null) {

                        var visits: Long = 0
                        var c: Long = 0
                        var cvisits: Long = 0

                        snapshot.forEach {
                            if (it.id.length == 10) {

                                if (c < mDays) {
                                    val count: Long = it.getLong("mNumVisits")!!
                                    visits += count
                                } else {
                                    val count: Long = it.getLong("mNumVisits")!!
                                    cvisits += count
                                }
                                c++

                            }
                        }
                        visits_count.text = " $visits"
                        if ((visits - cvisits > 0)) {
                            visits_comparison.text = "+${visits - cvisits} Vs ${getComparisonDateString()}"
                        } else {
                            visits_comparison.text = "${visits - cvisits} Vs ${getComparisonDateString()}"
                        }

                    } else {

                        visits_count.text = " New"

                    }
                })
    }

    /**
     * This function returns timestamp based on @param mDays
     */
    private fun getComparisonDateString(): String {

        return "${SimpleDateFormat("dd MMM").format(Date(Date().time - (mDays * 2 * 86400000L)))}" +
                " - ${SimpleDateFormat("dd MMM").format(Date(Date().time - (mDays * 86400000L)))}"
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
