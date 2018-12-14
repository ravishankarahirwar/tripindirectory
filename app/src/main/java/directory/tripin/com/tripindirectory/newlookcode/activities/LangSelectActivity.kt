package directory.tripin.com.tripindirectory.newlookcode.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import directory.tripin.com.tripindirectory.R
import directory.tripin.com.tripindirectory.manager.PreferenceManager
import kotlinx.android.synthetic.main.activity_lang_select.*

class LangSelectActivity : LocalizationActivity() {

    lateinit var context: Context
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        preferenceManager = PreferenceManager.getInstance(context)
        setContentView(R.layout.activity_lang_select)
        setListners()
    }

    private fun setListners() {

        saveandcont_lang.setOnClickListener {

            when(radioGroupLangs.checkedRadioButtonId){

                rbenglish.id ->{
                    setLanguage("en")
                    preferenceManager.preferredLang = 1
                }

                rbhindi.id ->{
                    setLanguage("hi")
                    preferenceManager.preferredLang = 2

                }

                rbmarathi.id ->{
                    setLanguage("mr")
                    preferenceManager.preferredLang = 3
                }
            }


        }

    }

    override fun onAfterLocaleChanged() {
        super.onAfterLocaleChanged()
        Toast.makeText(context,getString(R.string.language_changed),Toast.LENGTH_SHORT).show()
        finish()
    }
}
