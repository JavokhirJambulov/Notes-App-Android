package uz.javokhirjambulov.notes.commons

class Event<T>(private var myObject: T? = null) {
    var isHandled: Boolean = false

    fun setObject(myObject: T?) {
        isHandled = false
        this.myObject = myObject
    }

    fun getNotHandledObject(): T? {
        if (!isHandled) {
            isHandled = true
            return myObject
        }
        return null
    }

    fun getObject(): T? {
        isHandled = true
        return myObject
    }
}