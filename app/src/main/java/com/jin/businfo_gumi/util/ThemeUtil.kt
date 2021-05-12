package com.jin.businfo_gumi.util

import android.content.Context
import com.jin.businfo_gumi.MainApplication
import com.jin.businfo_gumi.R

@Suppress("UNUSED")
object ThemeUtil {
    fun setTheme(context: Context) {
        context.setTheme(
            when (MainApplication.settingPref.theme) {
                "RED" -> R.style.AppTheme_RED
                "ORANGE" -> R.style.AppTheme_ORANGE
                "YELLOW" -> R.style.AppTheme_YELLOW
                "GREEN" -> R.style.AppTheme_GREEN
                "BLUE" -> R.style.AppTheme_BLUE
                "PURPLE" -> R.style.AppTheme_PURPLE
                "PINK" -> R.style.AppTheme_PINK
                else -> R.style.AppTheme_GRAY
            }
        )
    }
}