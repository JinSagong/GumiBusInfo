package com.jin.businfo_gumi.util

import android.util.Log

@Suppress("UNUSED")
object Debug {
    private const val TAG = "androidJIN"

    fun i(msg: String) = Log.i(TAG, msg)
    fun e(msg: String) = Log.e(TAG, msg)

    fun apiRequest(msg: String) = Log.d("apiJ", "[REQUEST] $msg")
    fun apiResponse(msg: String) = Log.v("apiJ", "[RESPONSE] $msg")
    fun apiError(t: Throwable) = Log.e("apiJ", t.message.orEmpty())
}