package uz.javokhirjambulov.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import uz.javokhirjambulov.pomodoro.commons.Constants

class ActionReceiver :BroadcastReceiver() {
//    private val Constants = Constants()
    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.getStringExtra(Constants.BUTTON_ACTION)
            ?: throw AssertionError("Provide Button Action")

        when (action) {
            Constants.BUTTON_STOP-> {
                Constants.setNotificationButton(Constants.BUTTON_STOP)

            }
            Constants.BUTTON_START->{
                Constants.setNotificationButton(Constants.BUTTON_START)
            }

            Constants.BUTTON_PAUSE->{
                Constants.setNotificationButton(Constants.BUTTON_PAUSE)

            }

        }

    }
}