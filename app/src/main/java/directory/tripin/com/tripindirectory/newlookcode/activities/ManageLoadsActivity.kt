package directory.tripin.com.tripindirectory.newlookcode.activities

import android.arch.paging.PagedList
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.helper.Logger
import directory.tripin.com.tripindirectory.newlookcode.viewholders.LoadPostViewHolder
import directory.tripin.com.tripindirectory.newprofiles.models.LoadPostPojo
import kotlinx.android.synthetic.main.activity_fsload_board.*
import kotlinx.android.synthetic.main.activity_manage_loads.*
import kotlinx.android.synthetic.main.layout_fsyourloads_actionbar.*

class ManageLoadsActivity : AppCompatActivity() {

    lateinit var adapter: FirestorePagingAdapter<LoadPostPojo, LoadPostViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_loads)
        setListners()
    }

    private fun setListners() {

        backfslby.setOnClickListener {
            finish()
        }
        addnewload.setOnClickListener {
            val i = Intent(this, NewLoadFormActivity::class.java)
            startActivity(i)
        }
    }

    private fun setAdapter(filterLoadPostPojo: LoadPostPojo) {

        val bundle = Bundle()
        Logger.v(filterLoadPostPojo.toString())


        var baseQuery: Query = FirebaseFirestore.getInstance()
                .collection("loadposts")


        //fitler and sort
        baseQuery = baseQuery.whereEqualTo("mDetails.isSpammed", false)
        baseQuery = baseQuery.orderBy("mBidValue", Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.isActive", Query.Direction.DESCENDING)
        baseQuery =  baseQuery.orderBy("mDetails.mLastActive", Query.Direction.DESCENDING)
        baseQuery = baseQuery.orderBy("mDetails.mAvgRating", Query.Direction.DESCENDING)

        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(5)
                .build()

        val options = FirestorePagingOptions.Builder<LoadPostPojo>()
                .setLifecycleOwner(this)
                .setQuery(baseQuery, config, LoadPostPojo::class.java)
                .build()
        adapter = object : FirestorePagingAdapter<LoadPostPojo, LoadPostViewHolder>(options) {
            @NonNull
            override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): LoadPostViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_new_load_post, parent, false)
                return LoadPostViewHolder(view)
            }

            override fun onBindViewHolder(@NonNull holder: LoadPostViewHolder,
                                          position: Int,
                                          @NonNull model: LoadPostPojo) {

                if (model != null) {

                }


            }


            override fun onLoadingStateChanged(state: LoadingState) {
                when (state) {

                    LoadingState.LOADING_INITIAL -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.VISIBLE
                    }

                    LoadingState.LOADING_MORE -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.VISIBLE
                    }

                    LoadingState.LOADED -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                        loadingfslby.visibility = View.GONE

                    }

                    LoadingState.FINISHED ->{
                        loadingfslby.visibility = View.GONE
                    }

                    LoadingState.ERROR -> {
                        Logger.v("onLoadingStateChanged ${state.name}")
                    }

                }
            }
        }

        yourloadslist.layoutManager = LinearLayoutManager(this)
        yourloadslist.adapter = adapter


    }
}
