package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.pojos.InteractionPojo
import directory.tripin.com.tripindirectory.newlookcode.viewholders.RecentCallsViewHolder
import directory.tripin.com.tripindirectory.utils.TextUtils
import kotlinx.android.synthetic.main.activity_all_transporters.*
import kotlinx.android.synthetic.main.activity_recent_calls.*

class RecentCallsActivity : AppCompatActivity() {


    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context
    lateinit var textUtils: TextUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        textUtils = TextUtils()

        setContentView(R.layout.activity_recent_calls)
        setListners()
        setAdapter()

    }

    private fun setAdapter() {

        val query = FirebaseFirestore.getInstance()
                .collection("partners")
                .document(preferenceManager.userId)
                .collection("mCalls")
                .orderBy("mTimeStamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<InteractionPojo>()
                .setQuery(query, InteractionPojo::class.java)
                .setLifecycleOwner(this)
                .build()

        val adapter = object : FirestoreRecyclerAdapter<InteractionPojo, RecentCallsViewHolder>(options) {
            public override fun onBindViewHolder(holder: RecentCallsViewHolder, position: Int, model: InteractionPojo) {

                Logger.v("onBindViewHolder")

                if(model.getmUID() == preferenceManager.userId){
                    if(model.getmOcompanyName()!=null){
                        if(!model.getmOcompanyName().isEmpty()){
                            holder.name.text = textUtils.toTitleCase(model.getmOcompanyName())
                        }else{
                            holder.name.text = model.getmOdisplayName()
                        }
                    }else{
                        holder.name.text = model.getmOdisplayName()
                    }

                    holder.rmn.text = model.getmORMN()

                    holder.calltype_icon.setImageResource(R.drawable.ic_arrow_upward_black_24dp)

                    holder.itemView.setOnClickListener {
                        val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                        i.putExtra("uid",model.getmOUID())
                        i.putExtra("rmn",model.getmORMN())
                        i.putExtra("fuid",model.getmOFUID())
                        startActivity(i)
                    }

                }else{
                    if(model.getmCompanyName()!=null){
                        if(!model.getmCompanyName().isEmpty()){
                            holder.name.text = textUtils.toTitleCase(model.getmCompanyName())
                        }else{
                            holder.name.text = model.getmDisplayName()
                        }
                    }else{
                        holder.name.text = model.getmDisplayName()
                    }

                    holder.rmn.text = model.getmRMN()
                    holder.calltype_icon.setImageResource(R.drawable.ic_arrow_downward_black_24dp)

                    holder.itemView.setOnClickListener {
                        val i = Intent(context, CompanyProfileDisplayActivity::class.java)
                        i.putExtra("uid",model.getmUID())
                        i.putExtra("rmn",model.getmRMN())
                        i.putExtra("fuid",model.getmFUID())
                        startActivity(i)
                    }

                }


            }

            override fun onCreateViewHolder(group: ViewGroup, i: Int): RecentCallsViewHolder {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                val view = LayoutInflater.from(group.context)
                        .inflate(R.layout.item_recent_call, group, false)

                return RecentCallsViewHolder(view)
            }

            override fun onDataChanged() {
                super.onDataChanged()
            }

        }

        calleslist.layoutManager = LinearLayoutManager(this)
        calleslist.adapter = adapter
    }

    private fun setListners() {

        back_recentcalls.setOnClickListener {
            finish()
        }

    }
}
