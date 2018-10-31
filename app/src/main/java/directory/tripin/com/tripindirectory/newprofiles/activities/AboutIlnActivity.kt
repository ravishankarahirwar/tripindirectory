package directory.tripin.com.tripindirectory.newprofiles.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.chatingactivities.ChatRoomActivity
import directory.tripin.com.tripindirectory.helper.Logger
import kotlinx.android.synthetic.main.activity_about_iln.*

class AboutIlnActivity : AppCompatActivity() {
    lateinit var firebaseAnalytics : FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_iln)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setListners()
        Linkify.addLinks(fleetprovideriln, Linkify.ALL)
        Linkify.addLinks(loadprovideriln, Linkify.ALL)
        Linkify.addLinks(explaineriln, Linkify.ALL)



    }

    private fun setListners() {
        back_from_aboutiln.setOnClickListener {
            finish()
        }
        chat_from_about.setOnClickListener {
            chatwithassistant()
        }

        facebookll.setOnClickListener {
            //getOpenFacebookIntent(getApplicationContext());
            var FACEBOOK_PAGE_ID = "1288324944512615"
            try {
                val intent: Intent
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/$FACEBOOK_PAGE_ID"))
                startActivity(intent)
            } catch (e: Exception) {
                val intent: Intent
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/$FACEBOOK_PAGE_ID"))
                startActivity(intent)
            }
            val bundle = Bundle()
            bundle.putInt("platform",2)
            firebaseAnalytics.logEvent("z_social_clicked", bundle)

        }

        youtubell.setOnClickListener {
            val url = "https://www.youtube.com/channel/UClV9UBOuFJAVzGe8D3Y3xWw"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            val bundle = Bundle()
            bundle.putInt("platform",1)
            firebaseAnalytics.logEvent("z_social_clicked", bundle)
        }

        instagramll.setOnClickListener {
            val url = "https://www.instagram.com/indian.logistics.network/"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
            val bundle = Bundle()
            bundle.putInt("platform",3)
            firebaseAnalytics.logEvent("z_social_clicked", bundle)
        }

        fleetprovideriln.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("whichVideo",3)
            firebaseAnalytics.logEvent("z_video_tutorial_clicked", bundle)
        }
        loadprovideriln.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("whichVideo",2)
            firebaseAnalytics.logEvent("z_video_tutorial_clicked", bundle)
        }
        explaineriln.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("whichVideo",1)
            firebaseAnalytics.logEvent("z_video_tutorial_clicked", bundle)
        }
    }

    private fun chatwithassistant() {
        val intent = Intent(this@AboutIlnActivity, ChatRoomActivity::class.java)
        intent.putExtra("ormn", "+919284089759")
        intent.putExtra("imsg", "Hi, I am coming from About ILN page.")
        intent.putExtra("ouid", "pKeXxKD5HjS09p4pWoUcu8Vwouo1")
        intent.putExtra("ofuid", "4zRHiYyuLMXhsiUqA7ex27VR0Xv1")
        startActivity(intent)
        val bundle = Bundle()
        firebaseAnalytics.logEvent("z_assistant", bundle)
    }
}
