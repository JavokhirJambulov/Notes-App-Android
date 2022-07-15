package uz.javokhirjambulov.pomodoro.commons

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateUtils

object Constants {
    const val POMODORO_NOTIFICATION_ID = 42
    var currentStatus = TimerStatus.IN_PROGRESS
    var currentTimer = TimerType.SESSION_NOT_STARTED_YET
    var timeLeftString = ""
    var notificationStop = false
    var notificationPause = false
    var notificationResume = false

    val BUTTON_STOP = "button_stop"
    val BUTTON_START = "button_start"
    val BUTTON_PAUSE = "button_pause"

    val onIUpdateListener: MutableList<IUpdateListener> = mutableListOf()

    fun addListener(listener: IUpdateListener) {
        onIUpdateListener.add(listener)
    }

    fun removeListener(listener: IUpdateListener) {
        onIUpdateListener.remove(listener)
    }

    fun update() {
        for (listener in onIUpdateListener) {
            listener.onUpdate()
        }
    }

    fun setCurrentState(timerStatus: TimerStatus) {
        currentStatus = timerStatus
    }
    fun setCurrentTimerType(timerType: TimerType){
      currentTimer = timerType
    }
    fun setTimeLeftString(timeInSecond: Long){
        timeLeftString = DateUtils.formatElapsedTime(timeInSecond)
    }


    fun setNotificationButton(actionValue: String) {
        when (actionValue) {
            BUTTON_STOP -> {
                notificationStop = true
                notificationPause = false
                notificationResume = false

            }
            BUTTON_PAUSE -> {
                notificationStop = false
                notificationPause = true
                notificationResume = false
            }
            BUTTON_START -> {
                notificationStop = false
                notificationPause = false
                notificationResume = true
            }
        }
        update()
    }



    // Intent Action
    val BUTTON_ACTION = "button_action"
    // Source code
    const val sourceCodeURL: String = "https://github.com/AdrianMiozga/GetFlow"
    const val feedbackURL = "mailto:javohirjambulov@gmail.com?subject=Feedback about %s"
    //Used to vibrate
    val POMODORO_OVER_BUZZ_PATTERN = longArrayOf(200, 100, 200,100,200,100)
    val BREAK_OVER_BUZZ_PATTERN = longArrayOf(300, 200,300,200)
    val LONG_BREAK_OVER_BUZZ_PATTERN = longArrayOf(0, 500,0,500)
    val NO_BUZZ_PATTERN = longArrayOf(0)
    //used to Open the Intro Activity first
    const val FIRST_RUN = "pref_first_run"

}

interface IUpdateListener {
    fun onUpdate()
}
