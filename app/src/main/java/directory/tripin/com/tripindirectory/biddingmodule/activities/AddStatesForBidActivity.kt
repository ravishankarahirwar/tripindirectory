package directory.tripin.com.tripindirectory.biddingmodule.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.layout_addstates_actionbar.*

class AddStatesForBidActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_states_for_bid)
        setListners()
    }

    fun setListners(){

        backaddstates.setOnClickListener {
            finish()
        }

        addstatesdone.setOnClickListener {
            finish()
        }

    }
}
