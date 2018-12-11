package directory.tripin.com.tripindirectory.newlookcode.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

class ProfileRoleInputActivity : AppCompatActivity() {

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


    }

    override fun onResume() {
        super.onResume()
        if (preferenceManager.comapanyName != null) {
            if (preferenceManager.comapanyName.isNotEmpty()) {
                createprofiletypeinput.text = "See your Company Profile"
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

        if (preferenceManager.comapanyName != null) {

            if (preferenceManager.comapanyName.isNotEmpty()) {

                // update the mProfileType Value
                val hashMap = HashMap<String, String>()
                hashMap.put("mProfileType", profileType.toString())
                saveandcontprofileinput.text = "Saving..."
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

                                        Toast.makeText(context,"Try again!",Toast.LENGTH_SHORT).show()
                                        saveandcontprofileinput.text = "Save and continue"

                                    }
                        }.addOnCanceledListener {

                            Toast.makeText(context,"Try again!",Toast.LENGTH_SHORT).show()
                            saveandcontprofileinput.text = "Save and continue"

                        }

            }else{
                finish()
            }
        }else{
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
            type = "Load Provider"
        }
        if (profileType == 2L) {
            type = "Fleet Provider"
        }

        prettyDialog
                .setTitle("Continue as " + type)
                .setMessage("You can't change the selected role, you will have to contact ILN Assistant to edit it further. Are you sure?")
                .addButton(
                        "Yes! Goto ILN App.",
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    saveandContinue(profileType)

                }.addButton(
                        "Cancel",
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100
                ) {
                    prettyDialog.dismiss()

                }
        prettyDialog.show()


    }
}
