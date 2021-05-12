package com.jin.businfo_gumi.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.widget.Toasty

@Suppress("UNUSED")
object Network {
    private val cm by lazy { appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    fun checkConnection(showToast: Boolean = true) =
        check().also { if (!it && showToast) Toasty.alert(R.string.networkOffline) }

    @Suppress("DEPRECATION")
    private fun check(): Boolean {
        var result = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw =
                cm.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }
}