package uz.javokhirjambulov.pomodoro


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.format.DateUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.ActivityNavigator
import androidx.navigation.Navigation
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import uz.javokhirjambulov.pomodoro.commons.*
import uz.javokhirjambulov.pomodoro.databinding.MainScreenFragmentBinding
import uz.javokhirjambulov.pomodoro.screen.AboutActivity
import uz.javokhirjambulov.pomodoro.screen.MainIntroActivity
import uz.javokhirjambulov.pomodoro.screen.MainScreenViewModel
import kotlin.math.ln


@RequiresApi(Build.VERSION_CODES.M)
class MainActivity : AppCompatActivity(), IUpdateListener {


    private var POMODOR_DEFAULT_TIME = 20L

    //    private val constants = Constants()
    private val viewModel by viewModels<MainScreenViewModel>()

    private val notificationManager: MyNotificationManager by lazy {
        MyNotificationManager(applicationContext)
    }

    private lateinit var binding: MainScreenFragmentBinding
    private lateinit var preferencesPrivate: SharedPreferences



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        Log.i("MainActivity11","OnCreate")
        preferencesPrivate = this.getSharedPreferences(
            this.packageName + "_private_preferences",
            Context.MODE_PRIVATE
        )
        if(isFirstRun()){
            // show app intro
            val i = Intent(this, MainIntroActivity::class.java)
            startActivity(i)
            consumeFirstRun()
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.setContentView(this,
            R.layout.main_screen_fragment
        )
        binding.mainScreenViewModel = viewModel
        binding.bottomSheet.mainScreenViewModel = viewModel
        binding.lifecycleOwner = this
        setProgressTime(POMODOR_DEFAULT_TIME)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.root)


        binding.bottomSheet.pomodoroSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                // write custom code for progress is changed
                val pomodoroTime = (progress * 5L) + 10L
                viewModel.setPomodoroTime(pomodoroTime)
                setProgressTime(pomodoroTime)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })
        binding.bottomSheet.breakSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                // write custom code for progress is changed
                val breakTime = progress * 3L + 3L
                viewModel.setBreakTime(breakTime)
                setProgressTime(breakTime)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })
        binding.bottomSheet.longBreakSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                // write custom code for progress is changed
                val longBreakTime = progress * 5L + 5L
                viewModel.setLongBreakTime(longBreakTime)
                setProgressTime(longBreakTime)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })
        binding.bottomSheet.sessionsSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seek: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                // write custom code for progress is changed
                viewModel.setSessions(progress + 2L)

            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
            }
        })

        viewModel.timeInSecond.observe(this) { timeInSecond ->


            Constants.setTimeLeftString(timeInSecond)
            notificationManager.showNotification( Constants.timeLeftString,
                Constants.currentStatus,
                Constants.currentTimer)


            val timeTextAdapter =
                CircularProgressIndicator.ProgressTextAdapter { Constants.timeLeftString }
            binding.circularProgress.setProgressTextAdapter(timeTextAdapter)
            binding.circularProgress.setProgress(
                timeInSecond.toDouble(),
                viewModel.startTime().toDouble()
            )
        }


        viewModel.currentTimer.observe(this) { currentTimer ->
            Constants.setCurrentTimerType(currentTimer)
            notificationManager.showNotification(
                Constants.timeLeftString,
                Constants.currentStatus,currentTimer)
            when (currentTimer) {
                TimerType.SESSION_NOT_STARTED_YET->{
                    binding.timerType.text = getString(R.string.ready_for_new_session)
                }
                TimerType.POMODORO -> {
                    binding.timerType.text = getString(R.string.timer_type_pomodoro)
                }
                TimerType.BREAK -> {
                    binding.timerType.text = getString(R.string.timer_type_break)
                }
                TimerType.SESSION_COMPLETED -> {
                    binding.timerType.text = getString(R.string.timer_type_completed)
                }
                else -> {
                    binding.timerType.text = getString(R.string.timer_type_long_break)
                }
            }

        }


        viewModel.timerStatus.observe(this) { timerStatus ->
            Constants.setCurrentState(timerStatus)
            notificationManager.showNotification(Constants.timeLeftString,timerStatus, Constants.currentTimer)
            when (timerStatus) {
                TimerStatus.IN_PROGRESS -> {
                    visibleButton(TimerButton.PAUSE_BTN)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                TimerStatus.STOPPED -> {
                    visibleButton(TimerButton.START_BTN)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    notificationManager.clearNotification()
                }
                TimerStatus.PAUSED -> {
                    visibleButton(TimerButton.CONTINUE_BTN)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }

        }
        viewModel.buzzEvent.observe(this) { buzzEvent ->
            if(buzzEvent!= BuzzType.NO_BUZZ){
                buzz(buzzEvent.pattern)
                viewModel.onBuzzComplete()
            }
        }
        viewModel.soundEvent.observe(this){ soundEvent->
            if(soundEvent!= MediaType.NO_SOUND){
                playMedia(soundEvent)
                viewModel.onSoundComplete()
            }
        }


        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.coordinatorLayout.bringToFront()
            bottomSheetBehavior.isHideable = true
            bottomSheetBehavior.peekHeight = 150
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset > 0.1) {
                        binding.startButton.alpha = ((0.9 - slideOffset).toFloat())
                        binding.startButton.isEnabled = false
                    } else {
                        binding.startButton.alpha = 1F
                        binding.startButton.isEnabled = true
                    }
                }
            })
        }
        binding.menu.setOnClickListener{


            val popup = PopupMenu(this, binding.menu)

            popup.menuInflater.inflate(R.menu.menu, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.about -> {
                        startAboutActivity()


                        return@setOnMenuItemClickListener true
                    }
                }
                false
            }

            popup.show()
        }

    }

    private fun playMedia(soundEvent: MediaType?) {
        val MAX_VOLUME = 100
        val soundVolume = 10
        val volume: Double = (1 - (ln((MAX_VOLUME - soundVolume).toDouble()) / ln(
            MAX_VOLUME.toDouble()
        )))


        when(soundEvent){
            MediaType.POMODORO_OVER->{
                val mediaPlayer = MediaPlayer.create(this,R.raw.mixkit_phone_ring_bell)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()

            }
            MediaType.BREAK_OVER ->{
                val mediaPlayer = MediaPlayer.create(this,R.raw.mixkit_achievement_bell)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()
            }
            MediaType.LONG_BREAK_OVER->{
                val mediaPlayer = MediaPlayer.create(this,R.raw.mixkit_bell_of_promise)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()
            }
            else -> {}
        }

    }


    private fun startAboutActivity() {
        val intent = Intent(this, AboutActivity::class.java)
        this.startActivity(intent)
    }

    private fun setProgressTime(time:Long){
        val timeLeftString = DateUtils.formatElapsedTime(time)
        val timeTextAdapter =
            CircularProgressIndicator.ProgressTextAdapter { timeLeftString }
        binding.circularProgress.setProgressTextAdapter(timeTextAdapter)
        binding.circularProgress.setProgress(
            time.toDouble(),
            viewModel.startTime().toDouble()
        )
    }


    private fun visibleButton(button: TimerButton) {
        binding.continueButton.isVisible = button == TimerButton.CONTINUE_BTN
        binding.quitButton.isVisible = button == TimerButton.CONTINUE_BTN
        binding.pauseButton.isVisible = button == TimerButton.PAUSE_BTN
        binding.startButton.isVisible = button == TimerButton.START_BTN
        binding.menu.isVisible = button == TimerButton.START_BTN
    }

    override fun onUpdate() {
        when (true) {
            Constants.notificationPause -> {
                viewModel.pauseTimer()
            }
            Constants.notificationResume -> {
                viewModel.resumeTimer()
            }
            Constants.notificationStop -> {
                viewModel.stopTimer()
                notificationManager.clearNotification()
            }
            else -> {}
        }
    }
    private fun buzz(pattern: LongArray) {
        val buzzer = getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        Log.i("MainActivity","OnResume")
        Constants.addListener(this)
        notificationManager.showNotification( Constants.timeLeftString,
            Constants.currentStatus,
            Constants.currentTimer)
    }

    override fun onPause() {
        super.onPause()
        Log.i("MainActivity11","OnPause")
        Constants.removeListener(this)
    }

    override fun onStop() {
        super.onStop()
        Log.i("MainActivity11","OnStop")
        notificationManager.clearNotification()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.clearNotificationChannel()
//        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity11","OnDestroy")
//        notificationManager.clearNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.clearNotificationChannel()
        }
    }
    private fun isFirstRun() = preferencesPrivate.getBoolean(Constants.FIRST_RUN, true)

    private fun consumeFirstRun() = preferencesPrivate.edit().putBoolean(Constants.FIRST_RUN, false).apply()

}


