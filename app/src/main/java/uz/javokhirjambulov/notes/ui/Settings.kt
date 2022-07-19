package uz.javokhirjambulov.notes.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Constants
import uz.javokhirjambulov.notes.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity(){
    private lateinit var binding:ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.viewSourceCode.setOnClickListener{
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.sourceCodeURL))
            startActivity(openGithub)
        }
        binding.sendFeedback.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO,
                Uri.parse(String.format(Constants.feedbackURL, getString(R.string.app_name)))
            )

            startActivity(emailIntent)
        }
        binding.impExpCloud.setOnClickListener{

        }
//        binding.appIntro.setOnClickListener {
//            // show app intro
//            val i = Intent(this, MainIntroActivity::class.java)
//            startActivity(i)
//        }

    }
}