package com.jin.businfo_gumi.model.pref.storage

import android.content.Context
import android.content.SharedPreferences
import com.jin.businfo_gumi.model.pref.util.StringPreference

@Suppress("UNUSED")
class SettingPreferenceStorage(context: Context) {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences("setting", Context.MODE_PRIVATE)
    }

    var theme by StringPreference(prefs, "theme", "RED")

    fun reset() {
        theme = "RED"
    }
}