package uz.javokhirjambulov.notes.commons

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.javokhirjambulov.notes.R

object Dialog {
    fun progress() = ProgressDialogBuilder()

    class ProgressDialogBuilder {
        private var isCancelable = true

        private var onDismissAction: (() -> Unit)? = null

        fun dismissAction(action: () -> Unit): ProgressDialogBuilder {
            this.onDismissAction = action
            return this
        }

        fun cancelable(isCancelable: Boolean): ProgressDialogBuilder {
            this.isCancelable = isCancelable
            return this
        }

        fun show(activity: Activity?): AlertDialog? {
            activity ?: throw Exception("activity is null object return")
            val alertBuilder = AlertDialog.Builder(activity)
            var alert: AlertDialog? = null
            alertBuilder.setCancelable(isCancelable)
            val vs: View =
                LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null, false)

            alertBuilder.setTitle(null)

            alertBuilder.setOnDismissListener {
                onDismissAction?.invoke()
            }

            alertBuilder.setView(vs)
            return try {
                alert = alertBuilder.show()
                val params = alert?.window?.attributes
                params?.width = ViewGroup.LayoutParams.WRAP_CONTENT
                params?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                alert?.window?.attributes = params
                alert?.window?.setBackgroundDrawableResource(android.R.color.transparent)
                alert
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    }

}