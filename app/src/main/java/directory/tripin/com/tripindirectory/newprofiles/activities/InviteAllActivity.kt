package directory.tripin.com.tripindirectory.newprofiles.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.newlookcode.pojos.ContactPojo
import kotlinx.android.synthetic.main.activity_data_modifier.*
import kotlinx.android.synthetic.main.activity_invite_all.*

class InviteAllActivity : AppCompatActivity() {

    lateinit var doclist: QuerySnapshot


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_all)

        scandump.setOnClickListener {
            scandump()
        }

        invitenow.setOnClickListener {
            innvitedump()
        }
    }

    fun scandump() {

        not.text = "Getting Docs... Hold On!"
        FirebaseFirestore.getInstance().collection("promotionsdump")
                .whereGreaterThanOrEqualTo("mContactNumber","+917000023068")
                .whereEqualTo("invited",false)
                .limit(230)
                .get()
                .addOnCompleteListener {
                    not.text = it.result!!.size().toString()
                    doclist = it.result!!
                }
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                    Logger.v(it.size().toString())
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Failure", Toast.LENGTH_SHORT).show()

                }

    }

    fun innvitedump() {


        val batch = FirebaseFirestore.getInstance().batch()


        doclist.forEach {

            var contactPojo = it.toObject(ContactPojo::class.java)
            val ref = FirebaseFirestore.getInstance()
                    .collection("smsinvites")
                    .document(contactPojo.getmProviderRMN())
                    .collection("invitedcontacts").document(contactPojo.getmContactNumber())
            val ref2 = FirebaseFirestore.getInstance()
                    .collection("promotionsdump")
                    .document(contactPojo.getmContactNumber())
            batch.set(ref, contactPojo)
            batch.update(ref2,"invited",true)




        }

        batch.commit().addOnSuccessListener {
            Toast.makeText(applicationContext, "Commited!", Toast.LENGTH_SHORT).show()

        }.addOnCompleteListener {

        }.addOnFailureListener {

                       Toast.makeText(applicationContext, "Commit Failed!", Toast.LENGTH_SHORT).show()

        }


    }
}
