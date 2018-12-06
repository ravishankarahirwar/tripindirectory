package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.keiferstone.nonet.NoNet
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newprofiles.models.SubmittedReportPojo
import kotlinx.android.synthetic.main.activity_main_profile_insight.*
import kotlinx.android.synthetic.main.activity_submit_report.*
import kotlinx.android.synthetic.main.activity_user_edit_profile.*

class SubmitReportActivity : AppCompatActivity() {

    lateinit var context : Context
    lateinit var mCompUid : String
    lateinit var mCompFuid : String
    var mCompName : String = "Name"
    var mCompRMN : String = "***"
    lateinit var mCompPhotourl : String
    lateinit var preferenceManager: PreferenceManager
    lateinit var mReportTags: ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_report)

        setListners()
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        mReportTags = ArrayList()
        getIntentData()
        internetCheck()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            if (intent.extras.getString("uid") != null){
                mCompUid = intent.extras.getString("uid")
            }
            if (intent.extras.getString("name") != null){
                mCompName = intent.extras.getString("name")
                compname_submitreport.text = mCompName
            }
            if (intent.extras.getString("rmn") != null){
                mCompRMN = intent.extras.getString("rmn")
                rmn_submitreport.text = mCompRMN
            }
            if (intent.extras.getString("photourl") != null){
                mCompPhotourl = intent.extras.getString("photourl")
                setUpImage()
            }
            if (intent.extras.getString("fuid") != null){
                mCompFuid = intent.extras.getString("fuid")
            }
        }
        if(mCompUid==null){
            finish()
        }
    }

    private fun setUpImage() {
        if (mCompPhotourl != null) {
            if (!mCompPhotourl.isEmpty()) {
                Picasso.with(applicationContext)
                        .load(mCompPhotourl + "?width=160&width=160")
                        .placeholder(ContextCompat.getDrawable(applicationContext, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .into(userimage_submitreport, object : Callback {

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

        back_submitreport.setOnClickListener {
            finish()
        }

        submitreport_button.setOnClickListener {
            submitReport()
        }

        cbspam.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mReportTags!!.add(cbspam.text.toString())
            }else{
                mReportTags!!.remove(cbspam.text.toString())
            }
        }

        cbicall.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mReportTags!!.add(cbicall.text.toString())
            }else{
                mReportTags!!.remove(cbicall.text.toString())
            }
        }
        cbichat.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mReportTags!!.add(cbichat.text.toString())
            }else{
                mReportTags!!.remove(cbichat.text.toString())
            }
        }
        cbifraud.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mReportTags!!.add(cbifraud.text.toString())
            }else{
                mReportTags!!.remove(cbifraud.text.toString())
            }
        }

    }

    private fun submitReport() {

        if(mReportTags.isNotEmpty()){


            submitreport_button.text = "Sending..."
            var reportTags : List<String> = ArrayList()
            reportTags = mReportTags

            val submittedReportPojo = SubmittedReportPojo(preferenceManager.comapanyName,
                    preferenceManager.displayName,
                    preferenceManager.rmn,
                    preferenceManager.userId,
                    preferenceManager.fuid,
                    edittextreportcomment.text.toString().trim(),
                    mReportTags.size.toDouble(),
                    reportTags,mCompName,mCompName,mCompUid,mCompRMN,mCompFuid)


            FirebaseFirestore.getInstance()
                    .collection("adminappdata")
                    .document("reports")
                    .collection("reportedcomps")
                    .document(mCompUid)
                    .collection("reportslist").document(preferenceManager.userId).set(submittedReportPojo).addOnCompleteListener {

                        Toast.makeText(applicationContext,"Reported!",Toast.LENGTH_SHORT).show()
                        finish()

                    }.addOnCanceledListener {
                        submitreport_button.text = "Submit Report!"
                    }

        }else{

            Toast.makeText(applicationContext,"Select a cause",Toast.LENGTH_SHORT).show()

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
