package uz.javokhirjambulov.pomodoro.screen

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
import android.view.*
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import uz.javokhirjambulov.pomodoro.R
import uz.javokhirjambulov.pomodoro.commons.*
import uz.javokhirjambulov.pomodoro.databinding.MainScreenFragmentBinding
import kotlin.math.ln

@RequiresApi(Build.VERSION_CODES.M)
class MainScreenFragment : Fragment(), IUpdateListener {

    private var POMODOR_DEFAULT_TIME = 20L

    //    private val constants = Constants()
    private val viewModel by viewModels<MainScreenViewModel>()

    private val notificationManager: MyNotificationManager by lazy {
        MyNotificationManager(requireContext())
    }

    private lateinit var binding: MainScreenFragmentBinding
    private lateinit var preferencesPrivate: SharedPreferences



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferencesPrivate = requireContext().getSharedPreferences(
            context?.packageName + "_private_preferences",
            Context.MODE_PRIVATE
        )
        if(isFirstRun()){
                // show app intro
                val i = Intent(requireContext(), MainIntroActivity::class.java)
                startActivity(i)
                consumeFirstRun()
        }

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.main_screen_fragment,
            container,
            false
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
                viewModel.setBreakTime(progress * 3L + 3L)
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
                viewModel.setLongBreakTime(progress * 5L + 5L)
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

        viewModel.timeInSecond.observe(viewLifecycleOwner) { timeInSecond ->


            Constants.setTimeLeftString(timeInSecond)
            notificationManager.showNotification( Constants.timeLeftString,Constants.currentStatus,Constants.currentTimer)


            val timeTextAdapter =
                CircularProgressIndicator.ProgressTextAdapter { Constants.timeLeftString }
            binding.circularProgress.setProgressTextAdapter(timeTextAdapter)
            binding.circularProgress.setProgress(
                timeInSecond.toDouble(),
                viewModel.startTime().toDouble()
            )
        }


        viewModel.currentTimer.observe(viewLifecycleOwner) { currentTimer ->
            Constants.setCurrentTimerType(currentTimer)
            notificationManager.showNotification(Constants.timeLeftString,Constants.currentStatus,currentTimer)
            when (currentTimer) {
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


        viewModel.timerStatus.observe(viewLifecycleOwner) { timerStatus ->
            Constants.setCurrentState(timerStatus)
            notificationManager.showNotification(Constants.timeLeftString,timerStatus,Constants.currentTimer)
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
        viewModel.buzzEvent.observe(viewLifecycleOwner) { buzzEvent ->
            if(buzzEvent!=BuzzType.NO_BUZZ){
                buzz(buzzEvent.pattern)
                viewModel.onBuzzComplete()
            }
        }
        viewModel.soundEvent.observe(viewLifecycleOwner){ soundEvent->
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


            val popup = PopupMenu(requireContext(), binding.menu)

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

        return binding.root
    }

    private fun playMedia(soundEvent: MediaType?) {
        val MAX_VOLUME = 100
        val soundVolume = 10
        val volume: Double = (1 - (ln((MAX_VOLUME - soundVolume).toDouble()) / ln(
            MAX_VOLUME.toDouble()
        )))


        when(soundEvent){
            MediaType.POMODORO_OVER->{
                val mediaPlayer = MediaPlayer.create(requireContext(),R.raw.mixkit_phone_ring_bell)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()

            }
            MediaType.BREAK_OVER ->{
                val mediaPlayer = MediaPlayer.create(requireContext(),R.raw.mixkit_achievement_bell)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()
            }
            MediaType.LONG_BREAK_OVER->{
                val mediaPlayer = MediaPlayer.create(requireContext(),R.raw.mixkit_bell_of_promise)
                mediaPlayer.setVolume(volume.toFloat(), volume.toFloat())
                mediaPlayer.start()
            }
            else -> {}
        }

    }

    override fun onResume() {
        super.onResume()
        Constants.addListener(this)
    }
    private fun startAboutActivity() {
        val intent = Intent(requireContext(), AboutActivity::class.java)
        startActivity(intent)
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
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }




    override fun onPause() {
        super.onPause()
        Constants.removeListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()
        notificationManager.clearNotification()
        notificationManager.clearNotificationChannel()
    }
    private fun isFirstRun() = preferencesPrivate.getBoolean(Constants.FIRST_RUN, true)

    private fun consumeFirstRun() = preferencesPrivate.edit().putBoolean(Constants.FIRST_RUN, false).apply()
}

