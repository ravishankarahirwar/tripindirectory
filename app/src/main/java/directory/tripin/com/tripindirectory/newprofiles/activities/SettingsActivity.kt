package directory.tripin.com.tripindirectory.newprofiles.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import directory.tripin.com.tripindirectory.R
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_wallet.*

class SettingsActivity : AppCompatActivity() {

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
                Toast.makeText(applicationContext,"Will get Notifications",Toast.LENGTH_SHORT).show()
            }else{
                FirebaseMessaging.getInstance().unsubscribeFromTopic("loadboardNotification")
                Toast.makeText(applicationContext,"No notifications from Loadboard",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
