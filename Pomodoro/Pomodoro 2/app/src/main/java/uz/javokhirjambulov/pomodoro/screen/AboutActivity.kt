package uz.javokhirjambulov.pomodoro.screen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import uz.javokhirjambulov.pomodoro.R
import uz.javokhirjambulov.pomodoro.commons.Constants
import uz.javokhirjambulov.pomodoro.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity(){
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about)
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
        binding.appIntro.setOnClickListener {
            // show app intro
            val i = Intent(this, MainIntroActivity::class.java)
            startActivity(i)
        }



    }
}