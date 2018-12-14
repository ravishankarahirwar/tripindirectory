package directory.tripin.com.tripindirectory.newlookcode.activities

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_invite_phonebook.*
import android.provider.ContactsContract
import android.content.ContentResolver
import directory.tripin.com.tripindirectory.helper.Logger
import android.Manifest.permission
import android.Manifest.permission.READ_CONTACTS
import android.support.v4.app.ActivityCompat
import android.content.DialogInterface
import android.os.Build
import android.annotation.TargetApi
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.keiferstone.nonet.NoNet
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.ContactPojo
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.viewholders.RecentCallsViewHolder
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import kotlinx.android.synthetic.main.activity_recent_calls.*
import java.text.SimpleDateFormat


class InvitePhonebookActivity : LocalizationActivity() {

    private val PERMISSION_REQUEST_CONTACT: Int = 1
    lateinit var preferenceManager: PreferenceManager
    lateinit var adapter: FirestoreRecyclerAdapter<ContactPojo, RecentCallsViewHolder>
    lateinit var firebaseAnalytics :FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_phonebook)
        preferenceManager = PreferenceManager.getInstance(this)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setListners()
        setAdapter()
        internetCheck()


    }

    private fun setAdapter() {

        invitesloading.visibility = View.VISIBLE
        val query = FirebaseFirestore.getInstance()
                .collection("partners")
                .document(preferenceManager.userId)
                .collection("phonebook")
                .orderBy("mContactName", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<ContactPojo>()
                .setQuery(query, ContactPojo::class.java)
                .setLifecycleOwner(this)
                .build()

        adapter = object : FirestoreRecyclerAdapter<ContactPojo, RecentCallsViewHolder>(options) {
            public override fun onBindViewHolder(holder: RecentCallsViewHolder, position: Int, model: ContactPojo) {

                Logger.v("onBindViewHolder")
                holder.name.text = model.getmContactName()
                holder.rmn.text = model.getmContactNumber()

                if (model.alreadyonILN) {
                    holder.lable1.visibility = View.VISIBLE
                } else {
                    holder.lable1.visibility = View.GONE
                }

                if (model.invited) {
                    holder.time.text = getString(R.string.invited)
                    val top = holder.time.paddingTop
                    val left = holder.time.paddingLeft
                    val right = holder.time.paddingRight
                    val bottom = holder.time.paddingBottom
                    holder.time.background = ContextCompat.getDrawable(applicationContext, R.drawable.roundedcornerbg2)
                    holder.time.setPadding(left, top, right, bottom)

                } else {
                    holder.time.text = getString(R.string.invite)
                    val top = holder.time.paddingTop
                    val left = holder.time.paddingLeft
                    val right = holder.time.paddingRight
                    val bottom = holder.time.paddingBottom

                    holder.time.background = ContextCompat.getDrawable(applicationContext, R.drawable.border_sreoke_cyne_bg)
                    holder.time.setPadding(left, top, right, bottom)
                }

                holder.time.setOnClickListener {
                    holder.time.text = "..."
                    model.invited = true
                    FirebaseFirestore.getInstance()
                            .collection("partners")
                            .document(preferenceManager.userId)
                            .collection("phonebook").document(model.getmContactNumber()).set(model).addOnCompleteListener {
                                val bundle = Bundle()
                                firebaseAnalytics.logEvent("z_contact_invited", bundle)
                            }.addOnCanceledListener {
                                holder.time.text = getString(R.string.invite)
                                Toast.makeText(applicationContext, "Try Again!", Toast.LENGTH_SHORT).show()
                            }
                }

            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): RecentCallsViewHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view = LayoutInflater.from(group.context)
                        .inflate(R.layout.item_invite_contact, group, false)

                return RecentCallsViewHolder(view)
            }

            override fun onDataChanged() {
                super.onDataChanged()
                invitesloading.visibility = View.GONE

//                invite_permission.visibility = View.GONE
                skip_invite.text = getString(R.string.goto_iln_app)
                invite_permission.visibility = View.GONE
                contactslist.visibility = View.VISIBLE
                sync_contacts.visibility = View.VISIBLE


                if (itemCount == 0) {
                    allow_and_sync.text = getString(R.string.allow_and_sync)
                    skip_invite.text = getString(R.string.skip_now)
                    invite_permission.visibility = View.VISIBLE
                    contactslist.visibility = View.GONE
                    sync_contacts.visibility = View.GONE
                }

            }

        }

        contactslist.layoutManager = LinearLayoutManager(this)
        contactslist.adapter = adapter
    }

    override fun onBackPressed() {
    }

    private fun setListners() {

        allow_and_sync.setOnClickListener {
            Logger.v("askings permission")
            askForContactPermission()
            allow_and_sync.text = getString(R.string.hold_tight)
            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_contacts_synced", bundle)
        }

        sync_contacts.setOnClickListener {
            askForContactPermission()
        }

        skip_invite.setOnClickListener {
            if (checkBoxinvites.isChecked) {
                preferenceManager.settingPopupinvite = false
            }
            finish()
        }


    }

    private fun getContactList() {
        invitesloading.visibility = View.VISIBLE

        val cr = contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME))

                val timescontacted = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.TIMES_CONTACTED))

                if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id), null)


                    while (pCur!!.moveToNext()) {

                        val phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

//                        val email = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
//                        val website = pCur.getString(pCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Website.DATA))
//                        val organisation = pCur.getString(pCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Organization.DATA))
//                        val relation = pCur.getString(pCur.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Relation.DATA))

                        Logger.v("Name: $name")
                        Logger.v("Phone Number: $phoneNo")
                        Logger.v("Times Contacted: $timescontacted")


                        var contactno = phoneNo.replace("\\s".toRegex(), "")
                        if (!contactno.contains("+")) {
                            if (contactno.length.equals(10))
                                contactno = "+91" + contactno
                        }
                        val contactPojo = ContactPojo(contactno,
                                name, "", preferenceManager.displayName,
                                preferenceManager.rmn,
                                false, false, timescontacted, "", "", "")

                        var shoudUpload = true
                        adapter.snapshots.forEach {

                            if (it.getmContactNumber() == contactPojo.getmContactNumber()) {
                                shoudUpload = false
                            }

                        }

                        if (shoudUpload) {
                            FirebaseFirestore.getInstance()
                                    .collection("partners")
                                    .document(preferenceManager.userId)
                                    .collection("phonebook")
                                    .document(contactPojo.getmContactNumber()).set(contactPojo)
                                    .addOnCompleteListener {
                                        Logger.v("Name: $name uploaded")
                                    }

                        }


                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
        Logger.v("close")
        invitesloading.visibility = View.GONE
    }

    private fun askForContactPermission() {
        Logger.v("askings permission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_CONTACTS)) {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Contacts access needed")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("please confirm Contacts access")//TODO put real question
                    builder.setOnDismissListener {
                        requestPermissions(
                                arrayOf(Manifest.permission.READ_CONTACTS), PERMISSION_REQUEST_CONTACT)
                    }
                    builder.show()


                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            PERMISSION_REQUEST_CONTACT)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                getContactList()
            }
        } else {
            getContactList()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CONTACT -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContactList()
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(applicationContext, "No permission for contacts", Toast.LENGTH_LONG).show()
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
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
