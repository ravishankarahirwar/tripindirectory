package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.arch.paging.PagedList
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.ArrayAdapter
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import directory.tripin.com.tripindirectory.NewLookCode.BasicQueryPojo
import directory.tripin.com.tripindirectory.NewLookCode.PartnersViewHolder
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.activity.PartnerDetailScrollingActivity
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import kotlinx.android.synthetic.main.content_main_scrolling.*

class AllTransportersActivity : AppCompatActivity() {

    lateinit var adapter: FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo
    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transporters)
        context = this

        if(intent.extras!=null){
            if(intent.extras.getSerializable("query")!=null){
                basicQueryPojo = intent.extras.getSerializable("query") as BasicQueryPojo
                title = "${basicQueryPojo.mSourceCity} To ${basicQueryPojo.mDestinationCity}"
                var fleets: String = ""
                for(fleet:String in basicQueryPojo.mFleets!!){
                    fleets = "$fleets,$fleet"
                }
                supportActionBar!!.subtitle = fleets
                setMainAdapter(basicQueryPojo)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_alltrans, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_cancel -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun setMainAdapter( basicQueryPojo: BasicQueryPojo) {

        Logger.v(basicQueryPojo.toString())

        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("partners")

        if (!basicQueryPojo.mSourceCity.isEmpty()&& basicQueryPojo.mSourceCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mSourceCities.${basicQueryPojo.mSourceCity.toUpperCase()}", true)

        }
        if (!basicQueryPojo.mDestinationCity.isEmpty()&& basicQueryPojo.mDestinationCity != "Select City") {
            baseQuery = baseQuery.whereEqualTo("mDestinationCities.${basicQueryPojo.mDestinationCity.toUpperCase()}", true)

        }

        for (fleet in basicQueryPojo.mFleets!!) {
            baseQuery = baseQuery.whereEqualTo("fleetVehicle.$fleet", true)
        }
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<PartnerInfoPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, PartnerInfoPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): PartnersViewHolder {

                val view: View = if (viewType == 1) {
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_transporter_loading, parent, false)
                } else {
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.item_new_transporter, parent, false)
                }
                return PartnersViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: PartnersViewHolder,
                                          position: Int,
                                          @NonNull model: PartnerInfoPojo) {
                holder.mCompany.text = model.getmCompanyName()

                if (model != null) {
                    if (model.getmCompanyAdderss() != null)
                        holder.mAddress.text = model.getmCompanyAdderss().city
                }

                holder.itemView.setOnClickListener {

                    val i = Intent(context, PartnerDetailScrollingActivity::class.java)
                    i.putExtra("uid",getItem(position)!!.id)
                    i.putExtra("cname",model.getmCompanyName())
                    startActivity(i)
                }

                holder.mCall.setOnClickListener {


                    val phoneNumbers = java.util.ArrayList<String>()
                    val contactPersonPojos = model.getmContactPersonsList()

                    if (contactPersonPojos != null && contactPersonPojos!!.size > 1) {
                        for (i in contactPersonPojos!!.indices) {
                            if (model.getmContactPersonsList()[i] != null) {
                                val number = model.getmContactPersonsList()[i].getmContactPersonMobile
                                phoneNumbers.add(number)

                            }
                        }

                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Looks like there are multiple phone numbers.")
                                .setCancelable(false)
                                .setAdapter(ArrayAdapter(context, R.layout.dialog_multiple_no_row, R.id.dialog_number, phoneNumbers)
                                ) { dialog, item ->
                                    Logger.v("Dialog number selected :" + phoneNumbers[item])

                                    callNumber(phoneNumbers[item])
                                }

                        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, id: Int) {
                                // User cancelled the dialog
                            }
                        })
                        builder.create()
                        builder.show()


                    } else {

                        val number = model.getmContactPersonsList()[0].getmContactPersonMobile
                        callNumber(number)
                    }
                }


            }
            

            override fun getItemViewType(position: Int): Int {

                return if (position == itemCount - 1) {
                    1
                } else {
                    0
                }
            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")


                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")

                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        rv_transporters.layoutManager = LinearLayoutManager(this)
        rv_transporters.adapter = adapter


    }

    private fun callNumber(number: String) {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + Uri.encode(number.trim { it <= ' ' }))
        callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(callIntent)
    }
}
