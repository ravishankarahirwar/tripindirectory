package directory.tripin.com.tripindirectory.NewLookCode.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import kotlinx.android.synthetic.main.activity_ilnregister_ad.*
import android.content.Intent
import android.net.Uri


class ILNRegisterAdActivity : AppCompatActivity() {

    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ilnregister_ad)
        preferenceManager = PreferenceManager.getInstance(this)
        tryit.setOnClickListener {
            preferenceManager.setToShowRegAd(false)
            gotosignuplink()

        }

        nointerest.setOnClickListener {
            preferenceManager.setToShowRegAd(false)
            finish()
        }
    }

    private fun gotosignuplink() {
        val url = "http://indianlogisticsnetwork.com/register/#/auth/sign-up"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
        finish()
    }
}
