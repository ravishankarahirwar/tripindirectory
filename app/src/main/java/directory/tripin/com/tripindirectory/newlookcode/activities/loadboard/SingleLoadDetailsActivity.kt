package directory.tripin.com.tripindirectory.newlookcode.activities.loadboard

import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.appsee.Appsee
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.helper.CircleTransform
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import kotlinx.android.synthetic.main.activity_single_load_details.*
import kotlinx.android.synthetic.main.item_new_load_post.*
import kotlinx.android.synthetic.main.layout_loadetails_actionbar.*
import java.text.SimpleDateFormat

class SingleLoadDetailsActivity : AppCompatActivity() {

    /**
     * SingleLoadDetailsActivity renders deatiails of a single load from
     * firestore/loadposts/$loadid.
     *
     * increments the views on loadpost.
     *
     * shows number of views if its your own load.
     *
     * @author shubhamsardar
     */

    lateinit var loadPostPojo: LoadPostPojo
    lateinit var context: Context
    lateinit var preferenceManager: PreferenceManager
    lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var loadid: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_load_details)
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        fetchLoadData()
        backslbdetails.setOnClickListener {
            finish()
        }
        Appsee.start()
    }


    private fun fetchLoadData() {
        if (intent.extras != null) {
            if (intent.extras.getString("loadid") != null) {
                if (intent.extras.getString("loadid").isNotEmpty()) {
                    loadid = intent.extras.getString("loadid")
                    FirebaseFirestore
                            .getInstance()
                            .collection("loadposts")
                            .document(loadid)
                            .get()
                            .addOnSuccessListener {
                                if (it != null && it.exists()) {
                                    Logger.v("DocumentSnapshot data: " + it.data)
                                    loadPostPojo = it.toObject(LoadPostPojo::class.java)!!
                                    bindDetails(loadPostPojo)
                                } else {
                                    Logger.d("No such document")
                                    finish()
                                    Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener {
                                finish()
                                Toast.makeText(context, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                            }
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(intent.extras.getInt("notifid"))
                } else {
                    finish()
                    Toast.makeText(context, getString(R.string.moble_not_available), Toast.LENGTH_SHORT).show()
                }
            } else {
                finish()
                Toast.makeText(context, getString(R.string.moble_not_available), Toast.LENGTH_SHORT).show()
            }
        } else {
            finish()
            Toast.makeText(context, getString(R.string.moble_not_available), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * bind all the deatils from fetched load details in the UI
     */
    private fun bindDetails(model: LoadPostPojo) {
        if (model.getmUid().equals(preferenceManager.userId)) {
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.GONE
        }
        authername.text = model.getmDisplayName()
        source.text = model.getmSourceCity()
        destination.text = model.getmDestinationCity()
        truck_type.text = model.getmVehicleType()
        body_type.text = model.getmBodyType()
        weight.text = model.getmPayload() + " " + model.getmPayloadUnit()
        length.text = model.getmVehichleLenght() + " " + model.getmVehichleLenghtUnit()
        material.text = model.getmMaterial()
        post_requirement.text = model.getmRemark()
        date.text = SimpleDateFormat("dd MMM / HH:mm").format(model.getmTimeStamp())

        if (model.getmNumViews() != null) {
            if (preferenceManager.userId == model.getmUid()) {
                viewscount.visibility = View.VISIBLE
                viewscount.text = model.getmNumViews().toString()
            } else {
                viewscount.visibility = View.GONE
            }
        } else {
            viewscount.visibility = View.GONE
        }

        share.setOnClickListener {
            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_share_clicked_lb", bundle)
            shareMesssages(context, "Loadpost on ILN", model.toString())
        }

        call.setOnClickListener {

            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_call_clicked_lb", bundle)
            if (model.getmRmn() != null && model.getmRmn().isNotEmpty()) {
                callNumber(model.getmRmn())
            } else {
                Toast.makeText(context, getString(R.string.moble_not_available), Toast.LENGTH_SHORT).show()
            }
        }

        chat.setOnClickListener {

            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_chat_clicked_lb", bundle)
            val intent = Intent(context, ChatRoomActivity::class.java)
            intent.putExtra("imsg", model.toString())
            intent.putExtra("ormn", model.getmRmn())
            intent.putExtra("ouid", model.getmUid())
            intent.putExtra("ofuid", model.getmFuid())
            startActivity(intent)

        }

        delete.setOnClickListener {

            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        FirebaseFirestore.getInstance().collection("loadposts").document(loadid).delete().addOnCompleteListener {
                            Toast.makeText(context, "Removed Successfully", Toast.LENGTH_SHORT).show()
                        }
                        val bundle = Bundle()
                        firebaseAnalytics.logEvent("z_remove_clicked_lb", bundle)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Post")
            builder.setMessage("Are you sure? You want to delete this post.").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
        }

        autherprofile.setOnClickListener {
            val i = Intent(context, CompanyProfileDisplayActivity::class.java)
            i.putExtra("uid", model.getmUid())
            i.putExtra("rmn", model.getmRmn())
            i.putExtra("fuid", model.getmFuid())
            startActivity(i)
        }



        if (model.getmPhotoUrl() != null) {
            if (!model.getmPhotoUrl().isEmpty()) {
                Picasso.with(applicationContext)
                        .load(model.getmPhotoUrl())
                        .placeholder(ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round))
                        .transform(CircleTransform())
                        .fit()
                        .into(thumb, object : Callback {
                            override fun onSuccess() {
                                Logger.v("image set")
                            }

                            override fun onError() {
                                Logger.v("image error: " + model.getmPhotoUrl())
                            }

                        })
            } else {
                Logger.v("image url empty:")
            }
        } else {
            Logger.v("image url null:")
        }

        loaddetailslayout.visibility = View.VISIBLE
        incrimentview()

    }

    /**
     * function to increment the number of views on a loadpost
     */
    private fun incrimentview() {
        val interactionPojo = InteractionPojo(preferenceManager.userId,
                preferenceManager.fuid,
                preferenceManager.rmn,
                preferenceManager.comapanyName, preferenceManager.displayName,
                preferenceManager.fcmToken, loadPostPojo.getmUid(),
                loadPostPojo.getmFuid(),
                loadPostPojo.getmRmn(),
                loadPostPojo.getmCompanyName(), loadPostPojo.getmDisplayName(),
                "")

        if (loadid != null && loadid.isNotEmpty()) {

            if (preferenceManager.userId != null) {
                FirebaseFirestore.getInstance()
                        .collection("loadposts")
                        .document(loadid)
                        .collection("viewers")
                        .document(preferenceManager.userId).set(interactionPojo).addOnCompleteListener {
                            //view incrimented
                        }
            }

        }

    }

    /**
     * Method that makes sharing option available
     * @param context application context
     * @param subject the title of message
     * @param body the details of the loadpost converted into string
     */

    private fun shareMesssages(context: Context, subject: String, body: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            shareIntent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } catch (exception: ActivityNotFoundException) {
            Toast.makeText(context, "No application found for send Email", Toast.LENGTH_LONG).show()
        }

    }

    /**
     * Method that makes system call on the given mobile number
     * @param number The Given telephone number
     */

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(callIntent)
    }

}
