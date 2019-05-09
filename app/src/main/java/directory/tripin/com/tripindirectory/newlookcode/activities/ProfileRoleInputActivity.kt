package directory.tripin.com.tripindirectory.newlookcode.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import kotlinx.android.synthetic.main.activity_company_profile_edit.*
import kotlinx.android.synthetic.main.activity_profile_role_input.*
import libs.mjn.prettydialog.PrettyDialog
import java.util.HashMap

class ProfileRoleInputActivity : LocalizationActivity() {

    /**
     * Asks user to specify his role as
     * 1)Load Provider
     * 2)Fleet Provider
     * @author shubhamsardar
     */

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics: FirebaseAnalytics
    var profileType: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_role_input)
        context = this
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        preferenceManager = PreferenceManager.getInstance(context)
        setListners()
        setSelectedRole()

    }

    private fun setSelectedRole() {
        if (preferenceManager.profileType != 1L) {
            if (preferenceManager.profileType == 2L) {
                rblp.isChecked = false
                lbfp.isChecked = true
            }
            if (preferenceManager.profileType == 0L) {
                rblp.isChecked = true
                lbfp.isChecked = false
            }
        }
        if (intent.extras.getBoolean("isFromEditProfile") != null) {
            if (intent.extras.getBoolean("isFromEditProfile")) {
                profilelink.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (preferenceManager.comapanyName != null) {
            if (preferenceManager.comapanyName.isNotEmpty()) {
                createprofiletypeinput.text = getString(R.string.see_your_comp_profile)
            }
        }
    }

    private fun setListners() {
        createprofiletypeinput.setOnClickListener {
            val i = Intent(this, CompanyProfileDisplayActivity::class.java)
            i.putExtra("uid", preferenceManager.userId)
            i.putExtra("rmn", preferenceManager.rmn)
            i.putExtra("fuid", preferenceManager.fuid)
            startActivity(i)
            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_my_profile", bundle)
        }

        saveandcontprofileinput.setOnClickListener {

            if (radioGroupProfiletype.checkedRadioButtonId == rblp.id) {
                profileType = 0
            }
            if (radioGroupProfiletype.checkedRadioButtonId == lbfp.id) {
                profileType = 2
            }
            showprofiletypeconfermationdialog()
        }
    }

    private fun saveandContinue(profileType: Long) {
        preferenceManager.profileType = profileType

        val bundle = Bundle()
        bundle.putLong("profile_type", profileType)
        firebaseAnalytics.logEvent("z_my_profile_type", bundle)
        if (preferenceManager.comapanyName != null) {
            if (preferenceManager.comapanyName.isNotEmpty()) {
                // update the mProfileType Value
                val hashMap = HashMap<String, String>()
                hashMap.put("mProfileType", profileType.toString())
                saveandcontprofileinput.text = getString(R.string.saving)
                FirebaseFirestore.getInstance()
                        .collection("partners")
                        .document(preferenceManager.userId)
                        .set(hashMap as Map<String, Any>, SetOptions.merge())
                        .addOnCompleteListener {
                            saveandcontprofileinput.text = "Just a moment..."
                            val hashMap2 = HashMap<String, String>()
                            hashMap2.put("mProfileType", profileType.toString())
                            FirebaseFirestore.getInstance()
                                    .collection("denormalizers")
                                    .document(preferenceManager.userId)
                                    .set(hashMap2 as Map<String, Any>, SetOptions.merge())
                                    .addOnCompleteListener {
                                        finish()
                                    }.addOnCanceledListener {
                                        Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show()
                                        saveandcontprofileinput.text = getString(R.string.save_and_continue)
                                    }
                        }.addOnCanceledListener {
                            Toast.makeText(context, "Try again!", Toast.LENGTH_SHORT).show()
                            saveandcontprofileinput.text = getString(R.string.save_and_continue)
                        }

            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        Toast.makeText(context, "Tap on Save!", Toast.LENGTH_SHORT).show()
    }

    private fun showprofiletypeconfermationdialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        var type = ""
        if (profileType == 0L) {
            type = getString(R.string.load_provider)
        }
        if (profileType == 2L) {
            type = getString(R.string.fleet_provider)
        }

        prettyDialog
                .setTitle("Continue as " + type)
                .setMessage(getString(R.string.role_warning))
                .addButton(
                        getString(R.string.yes_goto_app),
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    saveandContinue(profileType)

                }.addButton(
                        getString(R.string.cancel),
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100
                ) {
                    prettyDialog.dismiss()

                }
        prettyDialog.show()


    }
}
