package com.jin.businfo_gumi.util

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams

@Suppress("UNUSED")
object StatusBar {
    private val typedValue = TypedValue()

    fun setStatusBarColor(act: Activity, @ColorRes colorId: Int) {
        act.window.statusBarColor = ContextCompat.getColor(act, colorId)
    }

    @Suppress("DEPRECATION")
    fun setTransparentStatusBar(act: Activity) {
        act.window.decorView.systemUiVisibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        act.window.statusBarColor = Color.TRANSPARENT
        val bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        val win = act.window
        val winParams = win.attributes
        winParams.flags = winParams.flags and bits.inv()
        win.attributes = winParams
    }

    fun setHeightOfStatusBar(act: Activity, statusBar: View) {
        statusBar.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                act.window.decorView.getWindowVisibleDisplayFrame(rect)
                val height = if (rect.top > 200) 0 else rect.top
                statusBar.updateLayoutParams<LinearLayout.LayoutParams> { this.height = height }
                statusBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}