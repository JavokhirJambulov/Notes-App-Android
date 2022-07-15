package uz.javokhirjambulov.pomodoro.commons



enum class BuzzType(val pattern: LongArray) {
    POMODORO_OVER(Constants.POMODORO_OVER_BUZZ_PATTERN),
    BREAK_OVER(Constants.BREAK_OVER_BUZZ_PATTERN),
    LONG_BREAK_OVER(Constants.LONG_BREAK_OVER_BUZZ_PATTERN),
    NO_BUZZ(Constants.NO_BUZZ_PATTERN)
}