package directory.tripin.com.tripindirectory.biddingmodule.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_all_india_bid.*
import kotlinx.android.synthetic.main.layout_allindiabid_actionbar.*

class AllIndiaBidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_india_bid)
        setListners()
    }

    private fun setListners() {
        backaib.setOnClickListener {
            finish()
        }

        doneaib.setOnClickListener {
            addcitiesfromstates()
        }
    }

    fun addcitiesfromstates(){
        finish()
    }

}
