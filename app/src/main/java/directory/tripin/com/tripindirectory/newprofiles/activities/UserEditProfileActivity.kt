package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.firebase.analytics.FirebaseAnalytics
import com.keiferstone.nonet.NoNet
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.activities.NewSplashActivity
import directory.tripin.com.tripindirectory.newlookcode.utils.MixPanelConstants
import directory.tripin.com.tripindirectory.utils.AppUtils
import kotlinx.android.synthetic.main.activity_user_edit_profile.*
import kotlinx.android.synthetic.main.layout_choose_vehicle.*
import kotlinx.android.synthetic.main.layout_main_actionbar.*
import kotlinx.android.synthetic.main.layout_route_input.*
import org.json.JSONException
import org.json.JSONObject

class UserEditProfileActivity : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var appUtils: AppUtils
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var mixpanelAPI: MixpanelAPI



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_edit_profile)
        context = this
        appUtils = AppUtils(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        preferenceManager = PreferenceManager.getInstance(context)
        mixpanelAPI = MixpanelAPI.getInstance(context,MixPanelConstants.MIXPANEL_TOKEN)
        setListners()
        internetCheck()

        if(!preferenceManager.isFacebooked){
            facebbokk.visibility = View.VISIBLE
        }

        if(!preferenceManager.isProfileIntroSeen){
            showIntro()
        }

    }

    override fun onResume() {
        super.onResume()
        setupView()
    }

    private fun setListners() {
        back.setOnClickListener {
            finish()
        }
        shareapp.setOnClickListener {
            appUtils.shareApp()
        }
        logout.setOnClickListener {
            AuthUI.getInstance().signOut(context).addOnSuccessListener {
                Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                preferenceManager.setisFacebboked(false)
                preferenceManager.rmn = null
                val i = Intent(this, NewSplashActivity::class.java)
                startActivity(i)
                finish()
                val bundle = Bundle()
                firebaseAnalytics.logEvent("z_logout", bundle)
                myprofileAction("logout")
            }
        }
        facebbokk.setOnClickListener {
            AuthUI.getInstance().signOut(context).addOnSuccessListener {
                Toast.makeText(context, "Logged Out", Toast.LENGTH_SHORT).show()
                preferenceManager.setisFacebboked(false)
                preferenceManager.rmn = null
                val i = Intent(this, NewSplashActivity::class.java)
                startActivity(i)
                finish()
                val bundle = Bundle()
                firebaseAnalytics.logEvent("z_logout", bundle)
            }
        }


        mybusinessll.setOnClickListener {
            val i = Intent(this, CompanyProfileDisplayActivity::class.java)
            i.putExtra("uid",preferenceManager.userId)
            i.putExtra("rmn",preferenceManager.rmn)
            i.putExtra("fuid",preferenceManager.fuid)
            startActivity(i)
            myprofileAction("my_business")
        }

        mynetworkll.setOnClickListener {
            val i = Intent(this, MyNetworkActivity::class.java)
            startActivity(i)
            myprofileAction("my_network")

        }
    }

    private fun setupView() {
        setUpImage()
        setUpDisplayName()
        setRMN()

    }

    private fun setRMN() {
        if (preferenceManager.rmn != null) {
            rmn.text = preferenceManager.rmn
        } else {
            rmn.text = "Error. No RMN found!"
        }
    }

    private fun setUpDisplayName() {
        if (preferenceManager.displayName != null) {
            username.text = preferenceManager.displayName
        } else {
            username.text = "ILN User"
        }
        if(preferenceManager.comapanyName!=null){
            textViewsubmb.text = preferenceManager.comapanyName
        }else{
            textViewsubmb.text = "..."
        }
    }

    private fun setUpImage() {
        if (preferenceManager.imageUrl != null) {
            if (!preferenceManager.imageUrl.isEmpty()) {
                Picasso.with(applicationContext)
                        .load(preferenceManager.imageUrl + "?width=160&width=160")
                        .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .into(userimage, object : Callback {

                            override fun onSuccess() {
                                Logger.v("image set: profile thumb")
                                Logger.v(preferenceManager.imageUrl)
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

    private fun internetCheck() {
        NoNet.monitor(this)
                .poll()
                .snackbar()
    }

    private fun showIntro() {

        val tapTargetSequence: TapTargetSequence = TapTargetSequence(this)
                .targets(
                        TapTarget.forView(imagemynet, "Your Logistics Network here", "See the List of Transporters you have added in your network, Tap on Target")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(false).outerCircleColor(R.color.primaryColor),
                        TapTarget.forView(imageViewmb, "Your Business Here", "Add and Manage your business profile from here. ")
                                .transparentTarget(true)
                                .drawShadow(true)
                                .cancelable(true).outerCircleColor(R.color.primaryColor)


                )
                .listener(object : TapTargetSequence.Listener {
                    override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                    }

                    override fun onSequenceFinish() {
                        preferenceManager.setisProfileIntroSeen(true)
                        val bundle = Bundle()
                        firebaseAnalytics.logEvent("z_profileguidefinished", bundle)
                        myprofileAction("intro_done")
                    }

                    override fun onSequenceCanceled(lastTarget: TapTarget) {
                        // Boo
                        preferenceManager.setisProfileIntroSeen(true)
                    }
                })

        tapTargetSequence.start()
    }

    private fun myprofileAction(action: String) {
        val props = JSONObject()
        try {
            props.put("action", action)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        mixpanelAPI.track(MixPanelConstants.EVENT_MYPROFILE_ACTION, props)
    }

}
