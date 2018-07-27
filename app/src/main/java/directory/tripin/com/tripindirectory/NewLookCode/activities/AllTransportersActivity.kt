package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.arch.paging.PagedList
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import directory.tripin.com.tripindirectory.NewLookCode.BasicQueryPojo
import directory.tripin.com.tripindirectory.NewLookCode.PartnersViewHolder
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo
import kotlinx.android.synthetic.main.content_main_scrolling.*

class AllTransportersActivity : AppCompatActivity() {

    lateinit var adapter: FirestorePagingAdapter<PartnerInfoPojo, PartnersViewHolder>
    lateinit var basicQueryPojo: BasicQueryPojo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transporters)
        title = "Mumbai To Rajkot"
        supportActionBar!!.subtitle = "LCV, Truck, Trailer"
        setMainAdapter(basicQueryPojo)
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

        val baseQuery = FirebaseFirestore.getInstance().collection("partners")
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


            }

            override fun getItemViewType(position: Int): Int {

                if (position == itemCount - 1) {
                    return 1
                } else {
                    return 0
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
}
