package uz.javokhirjambulov.notes.ui.screens

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import uz.javokhirjambulov.notes.R
import uz.javokhirjambulov.notes.commons.Constants


object AppRater {
    private const val APP_TITLE = "Notes" // App Name
    private const val APP_PNAME = "uz.javokhirjambulov.notes" // Package Name
    private const val DAYS_UNTIL_PROMPT = 1 //Min number of days
    private const val LAUNCHES_UNTIL_PROMPT = 3 //Min number of launches
    fun appLaunched(mContext: Context) {
        val prefs: SharedPreferences = mContext.getSharedPreferences("apprater", 0)
        if (prefs.getBoolean("dontshowagain", false)) {
            return
        }
        val editor = prefs.edit()

        // Increment launch counter
        val launch_count = prefs.getLong("launch_count", 0) + 1
        editor.putLong("launch_count", launch_count)

        // Get date of first launch
        var date_firstLaunch = prefs.getLong("date_firstlaunch", 0)
        if (date_firstLaunch == 0L) {
            date_firstLaunch = System.currentTimeMillis()
            editor.putLong("date_firstlaunch", date_firstLaunch)
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000
            ) {
                showRateDialog(mContext, editor)
            }
        }
        editor.commit()
    }

    private fun showRateDialog(mContext: Context, editor: SharedPreferences.Editor?) {
        val dialog = Dialog(mContext)
        dialog.setTitle("Rate $APP_TITLE")
        val ll = LinearLayout(mContext)
        ll.orientation = LinearLayout.VERTICAL
        ll.setPadding(10)
        val tv = TextView(mContext)
        tv.text =
            "If you enjoy using $APP_TITLE, please take a moment to rate it. Thanks for your support!"
        tv.width = 600
        tv.setPadding(4, 0, 4, 10)
        ll.addView(tv)
        val b1 = Button(mContext)
        b1.text = "Rate $APP_TITLE"
        b1.setOnClickListener {
            mContext.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.appURL)
                )
            )
            if (editor != null) {
                editor.putBoolean("dontshowagain", true)
                editor.commit()
            }
            dialog.dismiss()
        }
        ll.addView(b1)
        val b2 = Button(mContext)
        b2.text = "Remind me later"
        b2.setOnClickListener { dialog.dismiss() }
        ll.addView(b2)
        val b3 = Button(mContext)
        b3.text = "No, thanks"
        b3.setOnClickListener {
            if (editor != null) {
                editor.putBoolean("dontshowagain", true)
                editor.commit()
            }
            dialog.dismiss()
        }
        ll.addView(b3)
        dialog.setContentView(ll)
        dialog.show()
    }
}