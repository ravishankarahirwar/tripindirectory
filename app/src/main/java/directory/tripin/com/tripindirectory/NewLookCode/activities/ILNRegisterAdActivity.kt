package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import kotlinx.android.synthetic.main.activity_ilnregister_ad.*
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.keiferstone.nonet.NoNet
import directory.tripin.com.tripindirectory.NewLookCode.pojos.AdInterestedUser


class ILNRegisterAdActivity : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        setContentView(R.layout.activity_ilnregister_ad)
        preferenceManager = PreferenceManager.getInstance(this)
        tryit.setOnClickListener {
            preferenceManager.setToShowRegAd(false)
            gotosignuplink()
            val bundle = Bundle()
            bundle.putString("responce","Tried")
            firebaseAnalytics.logEvent("z_ad_ilnreg", bundle)
        }

        nointerest.setOnClickListener {
            preferenceManager.setToShowRegAd(false)
            val bundle = Bundle()
            bundle.putString("responce","No Interest")
            firebaseAnalytics.logEvent("z_ad_ilnreg", bundle)
            finish()
        }

        internetCheck()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val bundle = Bundle()
        bundle.putString("responce","Back Pressed")
        firebaseAnalytics.logEvent("z_ad_ilnreg", bundle)
    }

    private fun gotosignuplink() {

        tryit.text = "..."
        Toast.makeText(context,"Redirecting",Toast.LENGTH_SHORT).show()
        val adInterestedUser : AdInterestedUser = AdInterestedUser()
        adInterestedUser.setmAdname("ILN Register")
        adInterestedUser.setmUserRMN(preferenceManager.rmn)
        adInterestedUser.setmUserEmail(preferenceManager.email)
        adInterestedUser.setmUserName(preferenceManager.displayName)
        FirebaseFirestore.getInstance().collection("ad_interested").add(adInterestedUser).addOnSuccessListener {
            val url = "http://indianlogisticsnetwork.com/register/#/auth/sign-up"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            finish()
        }.addOnCanceledListener {
            Toast.makeText(context,"Please Try Again",Toast.LENGTH_LONG).show()
            tryit.text = "Try It!"
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
