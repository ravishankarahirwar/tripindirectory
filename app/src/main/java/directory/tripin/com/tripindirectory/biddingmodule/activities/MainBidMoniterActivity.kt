package directory.tripin.com.tripindirectory.biddingmodule.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.newprofiles.activities.WalletActivity
import kotlinx.android.synthetic.main.activity_main_bid_moniter.*
import kotlinx.android.synthetic.main.layout_mainbidmoniter_actionbar.*
import libs.mjn.prettydialog.PrettyDialog
import libs.mjn.prettydialog.PrettyDialogCallback

class MainBidMoniterActivity : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_bid_moniter)
        context = this
        setListners()
    }

    private fun setListners() {
        backmbm.setOnClickListener {
            finish()
        }

        publishbids.setOnClickListener {
            showPublishDialog()
        }

        seewallet.setOnClickListener {
            val i = Intent(this, WalletActivity::class.java)
            startActivity(i)
        }

        editallindiabid.setOnClickListener {
            val i = Intent(this, AllIndiaBidActivity::class.java)
            startActivity(i)
        }

        statewisebidedit.setOnClickListener {
            val i = Intent(this, StateWiseBidActivity::class.java)
            startActivity(i)
        }
    }

    private fun showPublishDialog() {

        val prettyDialog: PrettyDialog = PrettyDialog(this)

        prettyDialog
                .setTitle(getString(R.string.promote_my_business))
                .setMessage(getString(R.string.promotion_in_development_progress))
                .addButton(
                        getString(R.string.yes_talk_to_assistant),
                        R.color.pdlg_color_white,
                        R.color.green_400
                ) {
                    prettyDialog.dismiss()
                    publish()

                }.addButton(
                        getString(R.string.cancel),
                        R.color.pdlg_color_white,
                        R.color.blue_grey_100,
                        PrettyDialogCallback {
                            prettyDialog.dismiss()

                        }
                )
        prettyDialog.show()


    }

    private fun publish() {
        finish()
        Toast.makeText(context,"Published Successfully",Toast.LENGTH_SHORT).show()
    }
}
