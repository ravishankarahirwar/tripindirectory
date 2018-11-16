package directory.tripin.com.tripindirectory.newlookcode.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.keiferstone.nonet.NoNet
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.AdInterestedUser
import kotlinx.android.synthetic.main.activity_ilnregister_ad.*
import kotlinx.android.synthetic.main.activity_main_profile_insight.*
import kotlinx.android.synthetic.main.activity_new_offer.*

class NewOfferActivity : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        setContentView(R.layout.activity_new_offer)
        preferenceManager = PreferenceManager.getInstance(this)
        insuranceinterested.setOnClickListener {
            preferenceManager.setisInsuranceResponded(true)
            gotosignuplink()
            val bundle = Bundle()
            bundle.putString("responce","Interested")
            firebaseAnalytics.logEvent("z_offer_insurance", bundle)
        }

        nointerestinsurance.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("responce","No Interest")
            firebaseAnalytics.logEvent("z_offer_insurance", bundle)
            finish()
        }

        internetCheck()

        FirebaseFirestore.getInstance().collection("insurance_offer_interested").addSnapshotListener(this, EventListener<QuerySnapshot> { snapshot, e ->
            if (e != null) {
                finish()
                Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                return@EventListener
            }

            if (snapshot != null) {
                howmanyinterested.text = "${snapshot.size()+3} companies are interested!"
            } else {
                howmanyinterested.text = "counting..."
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val bundle = Bundle()

        bundle.putString("responce","Back Pressed")
        firebaseAnalytics.logEvent("z_offer_insurance", bundle)
    }

    private fun gotosignuplink() {

        val adInterestedUser : AdInterestedUser = AdInterestedUser()
        adInterestedUser.setmAdname("Insurance offer")
        adInterestedUser.setmUserRMN(preferenceManager.rmn)
        adInterestedUser.setmUserEmail(preferenceManager.email)
        adInterestedUser.setmUserName(preferenceManager.displayName)
        adInterestedUser.setmUserCompName(preferenceManager.comapanyName)
        FirebaseFirestore.getInstance().collection("insurance_offer_interested").add(adInterestedUser).addOnSuccessListener {
            Toast.makeText(context,"You will get a call soon!",Toast.LENGTH_SHORT).show()
            finish()
        }.addOnCanceledListener {
            Toast.makeText(context,"Please Try Again",Toast.LENGTH_LONG).show()
            insuranceinterested.text = "Interested!"
        }
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
