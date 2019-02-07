package directory.tripin.com.tripindirectory.biddingmodule.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_state_wise_bid.*
import kotlinx.android.synthetic.main.layout_statewisebid_actionbar.*

class StateWiseBidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_wise_bid)
        setListners()
    }

    private fun setListners() {

        donestates.setOnClickListener {
            finish()
        }

        backswb.setOnClickListener {
            finish()
        }

        addstates.setOnClickListener {

        }
    }
}
