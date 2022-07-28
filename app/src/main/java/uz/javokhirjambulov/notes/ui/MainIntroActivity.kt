package uz.javokhirjambulov.notes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.login.LoginActivity


@Suppress("DEPRECATION")
class MainIntroActivity: IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.organize_your_ideas)
                .description(R.string.write_down_your_thoughts)
                .image(R.drawable.intro1)
                .background(R.color.grey50)
                .backgroundDark(R.color.grey50)
                .layout(R.layout.activity_main_intro)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.use_one_account_everywhere)
                .description(R.string.multiple_phone)
                .image(R.drawable.intro5)
                .background(R.color.grey50)
                .backgroundDark(R.color.grey50)
                .layout(R.layout.activity_main_intro)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.simple_to_use)
                .description(R.string.easy_to_navigate)
                .image(R.drawable.intro2)
                .background(R.color.grey50)
                .backgroundDark(R.color.grey50)
                .layout(R.layout.activity_main_intro)
                .build()
        )
        addSlide(
            SimpleSlide.Builder()
                .title(R.string.intro_get_started)
                .description(R.string.keep_everything_ontrack)
                .image(R.drawable.intro3)
                .background(R.color.grey50)
                .backgroundDark(R.color.grey50)
                .layout(R.layout.activity_main_intro)
                .build()
        )
        buttonCtaClickListener = View.OnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

    }
}