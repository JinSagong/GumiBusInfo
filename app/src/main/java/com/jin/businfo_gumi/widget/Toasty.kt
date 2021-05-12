package com.jin.businfo_gumi.widget

import android.widget.Toast
import androidx.annotation.StringRes
import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.R
import com.muddzdev.styleabletoast.StyleableToast

@Suppress("UNUSED")
object Toasty {
    private var toast: StyleableToast? = null

    private fun messageToast(msg: String) = StyleableToast.makeText(
        appContext, msg, Toast.LENGTH_LONG, R.style.MsgToast
    )

    private fun alertToast(msg: String) = StyleableToast.makeText(
        appContext, msg, Toast.LENGTH_SHORT, R.style.AlertToast
    )

    fun cancel() = toast?.cancel()

    fun msg(@StringRes msgId: Int) {
        toast?.cancel()
        toast = messageToast(appContext.getString(msgId))
        toast!!.show()
    }

    fun msg(msg: String) {
        toast?.cancel()
        toast = messageToast(msg)
        toast!!.show()
    }

    fun alert(@StringRes msgId: Int) {
        toast?.cancel()
        toast = alertToast(appContext.getString(msgId))
        toast!!.show()
    }

    fun alert(msg: String) {
        toast?.cancel()
        toast = alertToast(msg)
        toast!!.show()
    }
}