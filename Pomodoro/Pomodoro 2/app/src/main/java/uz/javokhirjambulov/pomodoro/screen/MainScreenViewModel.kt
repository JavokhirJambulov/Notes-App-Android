package uz.javokhirjambulov.pomodoro.screen

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.javokhirjambulov.pomodoro.commons.BuzzType
import uz.javokhirjambulov.pomodoro.commons.MediaType
import uz.javokhirjambulov.pomodoro.commons.TimerStatus
import uz.javokhirjambulov.pomodoro.commons.TimerType

class MainScreenViewModel : ViewModel() {

    private var POMODOR_DEFAULT_TIME = 20L
    private var BREAK_DEFAULT_TIME = 3L
    private var LONG_BREAK_DEFAULT_TIME = 5L
    private var SESSION_DEFAULT = 2L

    private val _startTime = MutableLiveData<Long>()
    fun startTime(): Long = _startTime.value ?: 0L


    private val _timeInSecond = MutableLiveData<Long>()
    val timeInSecond: LiveData<Long> get() = _timeInSecond

    private val _timerStatus = MutableLiveData<TimerStatus>()
    val timerStatus: LiveData<TimerStatus> get() = _timerStatus

    private val _pomodoroTime = MutableLiveData(POMODOR_DEFAULT_TIME)
    val pomodoroTimeString: LiveData<String> =
        Transformations.map(_pomodoroTime) { time -> time.toString() }

    private val _breakTime = MutableLiveData(BREAK_DEFAULT_TIME)
    val breakTimeString: LiveData<String> =
        Transformations.map(_breakTime) { time -> time.toString() }

    private val _longBreakTime = MutableLiveData(LONG_BREAK_DEFAULT_TIME)
    val longBreakTimeString: LiveData<String> =
        Transformations.map(_longBreakTime) { time -> time.toString() }

    private val _sessions = MutableLiveData(SESSION_DEFAULT)
    val sessionsString: LiveData<String> =
        Transformations.map(_sessions) { time -> time.toString() }


    private val _sessionCounter = MutableLiveData(0L)


    private val _currentTimer = MutableLiveData<TimerType>(TimerType.SESSION_NOT_STARTED_YET)
    val currentTimer: LiveData<TimerType> get() = _currentTimer
    private var timerJob: Job? = null

    private var _buzzEvent = MutableLiveData<BuzzType>()
    val buzzEvent:LiveData<BuzzType>
        get() = _buzzEvent

    private var _soundEvent = MutableLiveData<MediaType>()
    val soundEvent:LiveData<MediaType>
        get() = _soundEvent

    fun setPomodoroTime(time: Long) {
        _pomodoroTime.value = time
    }

    fun setBreakTime(time: Long) {
        _breakTime.value = time
    }

    fun setLongBreakTime(time: Long) {
        _longBreakTime.value = time
    }

    fun setSessions(session: Long) {
        _sessions.value = session
    }


    private fun startTimer(time: Long) {
        _startTime.value = time
        _timeInSecond.value = time
        _timerStatus.value = TimerStatus.IN_PROGRESS

        startTimerJob()

    }


    fun pauseTimer() {
        _timerStatus.value = TimerStatus.PAUSED
        cancelTimerJob()
    }

    fun resumeTimer() {

        _timerStatus.value = TimerStatus.IN_PROGRESS

        startTimerJob()

    }

    fun stopTimer() {
        cancelTimerJob()
        _timeInSecond.value = 0
        _timerStatus.value = TimerStatus.STOPPED
    }


    private fun startTimerJob() {
        timerJob = viewModelScope.launch(Dispatchers.IO) {

            while ((_timeInSecond.value ?: 1) > 1) {
                delay(1000)
                viewModelScope.launch(Dispatchers.Main) {
                    _timeInSecond.value = _timeInSecond.value!! - 1L
                }
            }
            delay(1000)
            viewModelScope.launch(Dispatchers.Main) {
                checkTimer()


            }
        }


    }

    private fun checkTimer() {

        if(_currentTimer.value == TimerType.POMODORO){
            _sessionCounter.value = _sessionCounter.value?.plus(1L)
            _buzzEvent.value = BuzzType.POMODORO_OVER
            _soundEvent.value = MediaType.POMODORO_OVER
            if(_sessions.value ==_sessionCounter.value){
                _currentTimer.value = TimerType.LONG_BREAK
                startTimer(_longBreakTime.value?:LONG_BREAK_DEFAULT_TIME)
            }else{
            _currentTimer.value = TimerType.BREAK
            startTimer(_breakTime.value?:BREAK_DEFAULT_TIME)
            }
        } else if(_currentTimer.value == TimerType.BREAK){
            _buzzEvent.value = BuzzType.BREAK_OVER
            _soundEvent.value = MediaType.BREAK_OVER
            _currentTimer.value = TimerType.POMODORO
            startTimer(_pomodoroTime.value?:BREAK_DEFAULT_TIME)
        }else{
            _buzzEvent.value = BuzzType.LONG_BREAK_OVER
            _soundEvent.value = MediaType.LONG_BREAK_OVER
            _currentTimer.value = TimerType.SESSION_COMPLETED
            stopTimer()
        }


    }


    private fun cancelTimerJob() {
        if (timerJob?.isActive == true) {
            timerJob?.cancel()

        }
    }

    fun beginTimer() {
        _currentTimer.value = TimerType.POMODORO
        startTimer(_pomodoroTime.value ?: POMODOR_DEFAULT_TIME)
    }
    fun onBuzzComplete(){
        _buzzEvent.value = BuzzType.NO_BUZZ
    }
    fun onSoundComplete(){
        _soundEvent.value = MediaType.NO_SOUND
    }

}