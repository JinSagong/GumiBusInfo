package com.jin.businfo_gumi.util

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.CycleInterpolator

@Suppress("UNUSED")
fun getIconAnimator(view: View): ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
    addUpdateListener {
        view.translationY = -16 * (it.animatedValue as Float)
    }
    duration = 2000L
    interpolator = CycleInterpolator(0.5f)
    repeatCount = ValueAnimator.INFINITE
}