package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.SuperRequestPojo
import kotlinx.android.synthetic.main.activity_get_super.*
import kotlinx.android.synthetic.main.layout_activesuper_actionbar.*

class ActiveSuperPlan : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_super_plan)
        context = this

        preferenceManager = PreferenceManager.getInstance(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

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
                            if(superRequestPojo.getmRequestStatus().equals(1)){
                                //super user
                                planstatus.text = "Your SUPER is Active!"

                            }else{
                                //pending request
                                planstatus.text = "Hi, Your request is pending. You will get a call soon from ILN."
                            }
                        }

                    } else {

                    }
                })


    }

    private fun setListners() {

        backilnactive.setOnClickListener {
            finish()
        }
    }
}
