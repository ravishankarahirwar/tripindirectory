package directory.tripin.com.tripindirectory.newlookcode.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import com.algolia.instantsearch.helpers.Searcher
import com.algolia.instantsearch.helpers.InstantSearch
import com.keiferstone.nonet.NoNet.destroy
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.algolia.instantsearch.utils.ItemClickSupport
import com.google.firebase.analytics.FirebaseAnalytics
import directory.tripin.com.tripindirectory.newprofiles.activities.CompanyProfileDisplayActivity
import kotlinx.android.synthetic.main.activity_search_company.*


class SearchCompanyActivity : AppCompatActivity() {

    private val ALGOLIA_APP_ID = "RHELQ0ROWI"
    private val ALGOLIA_SEARCH_API_KEY = "63d588ea2c0d9e628cabfb1d6f0013fc"
    private val ALGOLIA_INDEX_NAME = "partners"
    lateinit var searcher: Searcher
    lateinit var context : Context
    lateinit var firebaseAnalytics: FirebaseAnalytics



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_search_company)
        searcher = Searcher.create(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME)
        val helper = InstantSearch(this, searcher)
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)


        c_search_result.setOnItemClickListener { recyclerView, position, v ->
            val hit = c_search_result.get(position)
            // Do something with the hit
            val i = Intent(context, CompanyProfileDisplayActivity::class.java)
            i.putExtra("uid",hit.getString("mUID"))
            i.putExtra("rmn",hit.getString("mRMN"))
            i.putExtra("fuid",hit.getString("mFUID"))
            startActivity(i)

            val bundle = Bundle()
            firebaseAnalytics.logEvent("z_search_company_card_tap", bundle)

        }


        search_company_text.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s:CharSequence, start:Int,
                                           count:Int, after:Int) {}
            override fun onTextChanged(s:CharSequence, start:Int,
                                       before:Int, count:Int) {
                if (s.isNotEmpty()){
                    no_result_info.visibility = View.GONE
                    c_search_result.visibility = View.VISIBLE
                    helper.search(s.toString())
                }else{
                    no_result_info.visibility = View.VISIBLE
                    c_search_result.visibility = View.GONE

                }
            }
        })
    }

    override fun onDestroy() {
        searcher.destroy()
        super.onDestroy()
    }

}
