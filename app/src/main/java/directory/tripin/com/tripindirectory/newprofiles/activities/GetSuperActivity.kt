package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.SuperRequestPojo
import kotlinx.android.synthetic.main.activity_company_profile_edit.*
import kotlinx.android.synthetic.main.activity_get_super.*
import kotlinx.android.synthetic.main.layout_getilnsuper_actionbar.*
import java.util.HashMap
import co.ceryle.radiorealbutton.RadioRealButton
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import directory.tripin.com.tripindirectory.R
import libs.mjn.prettydialog.PrettyDialog
import libs.mjn.prettydialog.PrettyDialogCallback


class GetSuperActivity : AppCompatActivity() {

    /**
     * Showcasing Super benefits and request
     * @author shubhamsardar
     */

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics: FirebaseAnalytics
    var selectedPlan: Long = 0
    lateinit var sliderLayout: SliderLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_super)
        context = this
        preferenceManager = PreferenceManager.getInstance(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        sliderLayout = findViewById(R.id.featureimages)
        setListners()
        listenRequestDocument()

    }

    private fun listenRequestDocument() {
        FirebaseFirestore.getInstance().collection("adminappdata")
                .document("requests")
                .collection("superrequests")
                .document(preferenceManager.userId).addSnapshotListener(this, EventListener<DocumentSnapshot> { snapshot, e ->
                    if (e != null) {
                        finish()
                        Toast.makeText(context, "Error, Try Again!", Toast.LENGTH_SHORT).show()
                        return@EventListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val superRequestPojo = snapshot.toObject(SuperRequestPojo::class.java)
                        if (superRequestPojo != null) {
                            if (superRequestPojo.getmProfileType() % 1 > 0) {
                                //super user
                                getsupernow.visibility = View.GONE
                                planselector.visibility = View.GONE
                                planstatus.visibility = View.VISIBLE
                                planstatus.text = "You are SUPER already"

                            } else {
                                //pending request
                                getsupernow.visibility = View.GONE
                                planselector.visibility = View.GONE
                                planstatus.visibility = View.VISIBLE
                                planstatus.text = "Hi, Your request is pending. You will recive a call from ILN shortly, " +
                                        "this is for verification purpose. You can enjoy the SUPER benifits then."
                            }
                        }

                    } else {

                        getsupernow.visibility = View.VISIBLE
                        planselector.visibility = View.VISIBLE
                        planstatus.visibility = View.GONE

                    }
                })

    }

    private fun setListners() {


        val defaultSliderView1: DefaultSliderView = DefaultSliderView(context)
        defaultSliderView1.image(R.drawable.superben1).scaleType = BaseSliderView.ScaleType.CenterInside
        sliderLayout.addSlider(defaultSliderView1)

        val defaultSliderView2: DefaultSliderView = DefaultSliderView(context)
        defaultSliderView2.image(R.drawable.superben2).scaleType = BaseSliderView.ScaleType.CenterInside
        sliderLayout.addSlider(defaultSliderView2)

        val defaultSliderView3: DefaultSliderView = DefaultSliderView(context)
        defaultSliderView3.image(R.drawable.superben3).scaleType = BaseSliderView.ScaleType.CenterInside
        sliderLayout.addSlider(defaultSliderView3)

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default)
        sliderLayout.setDuration(3000)
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)

        getsupernow.setOnClickListener {
            // perform write
            showpromotedialog()
        }

        chatwithassistant.setOnClickListener {
            chatwithassistant()
        }

        backilnsuper.setOnClickListener {
            finish()
        }

        planselector.setOnClickedButtonListener { button, position ->
            selectedPlan = position.toLong()
        }

    }

    private fun chatwithassistant() {

        val intent = Intent(this@GetSuperActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("imsg", "Hi, I want to know more about ILN Super.")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)

    }

    private fun showpromotedialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle("Request ILN SUPER")
                .setMessage("Your will get a verification call on your registered mobile number. After which you can enjoy SUPER benefits. Are you sure to about this? ")
                .addButton(
                        "YES! Request ILN SUPER.",
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()

                    val superRequestPojo = SuperRequestPojo(preferenceManager.userId,
                            preferenceManager.fuid, preferenceManager.rmn,
                            preferenceManager.profileType,
                            preferenceManager.comapanyName,
                            preferenceManager.displayName, 0, selectedPlan)

                    FirebaseFirestore.getInstance().collection("adminappdata")
                            .document("requests")
                            .collection("superrequests")
                            .document(superRequestPojo.getmUid())
                            .set(superRequestPojo).addOnCompleteListener {

                                Toast.makeText(context, "Requested Successfully", Toast.LENGTH_SHORT).show()
                            }

                }.addButton(
                        getString(R.string.cancel),
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100,
                        PrettyDialogCallback {
                            prettyDialog.dismiss()

                        }
                )
        prettyDialog.show()

    }
}
