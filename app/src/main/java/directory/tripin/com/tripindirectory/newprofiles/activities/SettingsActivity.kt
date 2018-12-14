package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.firebase.messaging.FirebaseMessaging
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.newlookcode.activities.LangSelectActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_wallet.*

class SettingsActivity : LocalizationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setListners()
    }

    private fun setListners() {
        back_settings.setOnClickListener {
            finish()
        }

        switch_lb_notif.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                FirebaseMessaging.getInstance().subscribeToTopic("loadboardNotification")
                Toast.makeText(applicationContext,getString(R.string.you_will_be_notified_lb),Toast.LENGTH_SHORT).show()
            }else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic("loadboardNotification")
                Toast.makeText(applicationContext,getString(R.string.no_lb_notif),Toast.LENGTH_SHORT).show()
            }
        }

        changelang.setOnClickListener {
            val intent = Intent(this@SettingsActivity, LangSelectActivity::class.java)
            startActivity(intent)
        }
    }
}
