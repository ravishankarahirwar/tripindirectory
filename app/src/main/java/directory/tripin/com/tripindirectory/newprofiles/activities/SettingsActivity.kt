package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.firebase.messaging.FirebaseMessaging
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import directory.tripin.com.tripindirectory.newlookcode.activities.LangSelectActivity
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_wallet.*

class SettingsActivity : LocalizationActivity() {

    /**
     * This is to allow users change app settings
     * 1) language
     * 2) load board notifications
     * @author shubhamsardar
     */

    lateinit var preferenceManager: PreferenceManager
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this
        preferenceManager = PreferenceManager.getInstance(this)

        setContentView(R.layout.activity_settings)
        setListners()

    }

    private fun setListners() {
        back_settings.setOnClickListener {
            finish()
        }

        switch_lb_notif.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("newloadposts")
                Toast.makeText(applicationContext, getString(R.string.you_will_be_notified_lb), Toast.LENGTH_SHORT).show()
                preferenceManager.settingLoadboardNotif = true
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("newloadposts")
                Toast.makeText(applicationContext, getString(R.string.no_lb_notif), Toast.LENGTH_SHORT).show()
                preferenceManager.settingLoadboardNotif = false
            }
        }
        setSelectedLbNotifPref()

        changelang.setOnClickListener {
            val intent = Intent(this@SettingsActivity, LangSelectActivity::class.java)
            startActivity(intent)
        }
        setSelectedLang()
    }

    private fun setSelectedLbNotifPref() {

        switch_lb_notif.isChecked = preferenceManager.settingLoadboardNotif

    }

    private fun setSelectedLang() {
        when (preferenceManager.preferredLang) {
            1L -> {
                preferedlang.text = "English"
            }
            2L -> {
                preferedlang.text = "Hindi"
            }
            3L -> {
                preferedlang.text = "Marathi"
            }
        }
    }
}
