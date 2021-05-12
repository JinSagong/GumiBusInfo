package com.jin.businfo_gumi.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.jin.businfo_gumi.MainApplication.Companion.settingPref
import com.jin.businfo_gumi.R
import kotlinx.android.synthetic.main.item_theme.view.*
import kotlinx.android.synthetic.main.layout_fab.view.*
import kotlin.math.abs

class MainFab : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context, attrs, defStyle
    ) {
        init(context)
    }

    private lateinit var view: View

    private var themeView: ImageView? = null

    private var onClickFavoriteListener: (() -> Unit)? = null
    private var onClickGPSListener: (() -> Unit)? = null
    private var onClickThemeListener: ((Resources.Theme) -> Unit)? = null

    private val aniOpen by lazy { AnimationUtils.loadAnimation(context, R.anim.fab_open) }
    private val aniClose by lazy { AnimationUtils.loadAnimation(context, R.anim.fab_close) }

    private var isThemeFabOpen = false
    private var isFabMoved = false

    private fun init(context: Context) {
        view = View.inflate(context, R.layout.layout_fab, null)
        addView(view)
    }

    fun doOnClickFavorite(l: () -> Unit) = apply { onClickFavoriteListener = l }
    fun doOnClickGPS(l: () -> Unit) = apply { onClickGPSListener = l }
    fun doOnClickTheme(l: (Resources.Theme) -> Unit) = apply { onClickThemeListener = l }
    fun fetch() {
        setSelectThemeFab()
        setMovingAnimation()
    }

    private fun setSelectThemeFab() {
        val mOnClickThemeListener = OnClickListener {
            if (themeView == null || it != themeView) {
                themeView = it as ImageView
                context.setTheme(
                    when (it) {
                        themeFab1 -> {
                            settingPref.theme = "RED"
                            R.style.AppTheme_RED
                        }
                        themeFab2 -> {
                            settingPref.theme = "ORANGE"
                            R.style.AppTheme_ORANGE
                        }
                        themeFab3 -> {
                            settingPref.theme = "YELLOW"
                            R.style.AppTheme_YELLOW
                        }
                        themeFab4 -> {
                            settingPref.theme = "GREEN"
                            R.style.AppTheme_GREEN
                        }
                        themeFab5 -> {
                            settingPref.theme = "BLUE"
                            R.style.AppTheme_BLUE
                        }
                        themeFab6 -> {
                            settingPref.theme = "PURPLE"
                            R.style.AppTheme_PURPLE
                        }
                        themeFab7 -> {
                            settingPref.theme = "PINK"
                            R.style.AppTheme_PINK
                        }
                        themeFab8 -> {
                            settingPref.theme = "GRAY"
                            R.style.AppTheme_GRAY
                        }
                        else -> throw IllegalArgumentException()
                    }
                )
                val bg =
                    ResourcesCompat.getDrawable(resources, R.drawable.fab_button, context.theme)
                favoriteFab.background = bg
                gpsFab.background = bg
                themeFab.background = bg
                onClickThemeListener?.invoke(context.theme)
            }
        }

        themeFab1.setOnClickListener(mOnClickThemeListener)
        themeFab2.setOnClickListener(mOnClickThemeListener)
        themeFab3.setOnClickListener(mOnClickThemeListener)
        themeFab4.setOnClickListener(mOnClickThemeListener)
        themeFab5.setOnClickListener(mOnClickThemeListener)
        themeFab6.setOnClickListener(mOnClickThemeListener)
        themeFab7.setOnClickListener(mOnClickThemeListener)
        themeFab8.setOnClickListener(mOnClickThemeListener)
    }

    private fun setFavoriteFab() {
        onClickFavoriteListener?.invoke()
    }

    private fun setGpsFab() {
        onClickGPSListener?.invoke()
    }

    private fun setThemeFab() {
        if (isThemeFabOpen) {
            themeFab1.startAnimation(aniClose)
            themeFab2.startAnimation(aniClose)
            themeFab3.startAnimation(aniClose)
            themeFab4.startAnimation(aniClose)
            themeFab5.startAnimation(aniClose)
            themeFab6.startAnimation(aniClose)
            themeFab7.startAnimation(aniClose)
            themeFab8.startAnimation(aniClose)
            themeFab1.isClickable = false
            themeFab2.isClickable = false
            themeFab3.isClickable = false
            themeFab4.isClickable = false
            themeFab5.isClickable = false
            themeFab6.isClickable = false
            themeFab7.isClickable = false
            themeFab8.isClickable = false
        } else {
            themeFab1.startAnimation(aniOpen)
            themeFab2.startAnimation(aniOpen)
            themeFab3.startAnimation(aniOpen)
            themeFab4.startAnimation(aniOpen)
            themeFab5.startAnimation(aniOpen)
            themeFab6.startAnimation(aniOpen)
            themeFab7.startAnimation(aniOpen)
            themeFab8.startAnimation(aniOpen)
            themeFab1.isClickable = true
            themeFab2.isClickable = true
            themeFab3.isClickable = true
            themeFab4.isClickable = true
            themeFab5.isClickable = true
            themeFab6.isClickable = true
            themeFab7.isClickable = true
            themeFab8.isClickable = true
        }
        isThemeFabOpen = !isThemeFabOpen
    }

    private fun createSpringAnimation(
        view: View, property: DynamicAnimation.ViewProperty
    ): SpringAnimation {
        val animation = SpringAnimation(view, property)
        val spring = SpringForce()
        spring.stiffness = SpringForce.STIFFNESS_LOW
        spring.dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        animation.spring = spring

        return animation
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setMovingAnimation() {
        val xAnimationFavorite =
            createSpringAnimation(favoriteFab, SpringAnimation.X)
        val yAnimationFavorite =
            createSpringAnimation(favoriteFab, SpringAnimation.Y)
        val xAnimationGps =
            createSpringAnimation(gpsFab, SpringAnimation.X)
        val yAnimationGps =
            createSpringAnimation(gpsFab, SpringAnimation.Y)
        val xAnimationTheme =
            createSpringAnimation(themeFab, SpringAnimation.X)
        val yAnimationTheme =
            createSpringAnimation(themeFab, SpringAnimation.Y)
        val xAnimationThemeLayout =
            createSpringAnimation(themeLayout, SpringAnimation.X)
        val yAnimationThemeLayout =
            createSpringAnimation(themeLayout, SpringAnimation.Y)

        val margin = resources.getDimensionPixelSize(R.dimen.marginDouble).toFloat()

        var initX = 0f
        var initY = 0f
        var dX = 0f
        var dY = 0f
        var tX: Float
        var tY: Float
        var hasMovedFavorite = false
        var hasMovedGps = false
        var hasMovedTheme = false
        var hasTouched: View? = null

        val mOnMovingTouchListener = OnTouchListener { v, event ->
            tX =
                (event.rawX + dX).let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it }
            tY =
                (event.rawY + dY).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (hasTouched != null) return@OnTouchListener true
                    hasTouched = v
                    initX = event.rawX
                    initY = event.rawY
                    dX = v.x - initX
                    dY = v.y - initY
                    when (v) {
                        favoriteFab -> hasMovedFavorite = false
                        gpsFab -> hasMovedGps = false
                        themeFab -> hasMovedTheme = false
                    }
                    xAnimationFavorite.cancel()
                    yAnimationFavorite.cancel()
                    xAnimationGps.cancel()
                    yAnimationGps.cancel()
                    xAnimationTheme.cancel()
                    yAnimationTheme.cancel()
                    xAnimationThemeLayout.cancel()
                    yAnimationThemeLayout.cancel()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (hasTouched != v) return@OnTouchListener true
                    v.animate()
                        .x(tX)
                        .y(tY)
                        .setDuration(0)
                        .start()
                    if ((abs(event.rawX - initX) > v.width / 4) || (abs(event.rawY - initY) > v.height / 4)) {
                        if (isThemeFabOpen) {
                            isFabMoved = true
                            setThemeFab()
                        }
                        when (v) {
                            favoriteFab -> hasMovedFavorite = true
                            gpsFab -> hasMovedGps = true
                            themeFab -> hasMovedTheme = true
                        }
                    }

                    if (tX > fabBackground.width / 2 - v.width / 2)
                        xAnimationThemeLayout.animateToFinalPosition(tX - themeLayout.width)
                    else xAnimationThemeLayout.animateToFinalPosition(tX + v.width)

                    when (v) {
                        favoriteFab -> {
                            xAnimationGps.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationGps.animateToFinalPosition(
                                (tY + v.height + margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            xAnimationTheme.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationTheme.animateToFinalPosition(
                                (tY + 2 * v.height + 2 * margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            yAnimationThemeLayout.animateToFinalPosition(
                                (tY + 2 * v.height + 2 * margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                        }
                        gpsFab -> {
                            xAnimationFavorite.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationFavorite.animateToFinalPosition(
                                (tY - v.height - margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            xAnimationTheme.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationTheme.animateToFinalPosition(
                                (tY + v.height + margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            yAnimationThemeLayout.animateToFinalPosition(
                                (tY + v.height + margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                        }
                        themeFab -> {
                            xAnimationFavorite.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationFavorite.animateToFinalPosition(
                                (tY - 2 * v.height - 2 * margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            xAnimationGps.animateToFinalPosition(tX.let { if (it < 0f) 0f else if (it > fabBackground.width - v.width) (fabBackground.width - v.width).toFloat() else it })
                            yAnimationGps.animateToFinalPosition(
                                (tY - v.height - margin).let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it }
                            )
                            yAnimationThemeLayout.animateToFinalPosition(tY.let { if (it < 0f) 0f else if (it > fabBackground.height - v.height) (fabBackground.height - v.height).toFloat() else it })
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (hasTouched != v) return@OnTouchListener true
                    when {
                        v == favoriteFab && !hasMovedFavorite -> setFavoriteFab()
                        v == gpsFab && !hasMovedGps -> setGpsFab()
                        v == themeFab && !hasMovedTheme -> setThemeFab()
                        isFabMoved -> {
                            setThemeFab()
                            isFabMoved = false
                        }
                    }

                    var outside = 0
                    when (v) {
                        favoriteFab -> if (tY <= 0f) outside = 1
                        else if (tY >= (fabBackground.height - 3 * v.height - 2 * margin))
                            outside = 2
                        gpsFab -> if (tY <= v.height + margin) outside = 1
                        else if (tY >= (fabBackground.height - 2 * v.height - 1 * margin))
                            outside = 2
                        themeFab -> if (tY <= 2 * v.height + 2 * margin) outside = 1
                        else if (tY >= (fabBackground.height - v.height).toFloat())
                            outside = 2
                    }
                    if (outside == 1) {
                        yAnimationFavorite.animateToFinalPosition(0f)
                        yAnimationGps.animateToFinalPosition(v.height + margin)
                        yAnimationTheme.animateToFinalPosition(2 * v.height + 2 * margin)
                        yAnimationThemeLayout.animateToFinalPosition(2 * v.height + 2 * margin)
                    } else if (outside == 2) {
                        yAnimationFavorite.animateToFinalPosition(fabBackground.height - 3 * v.height - 2 * margin)
                        yAnimationGps.animateToFinalPosition(fabBackground.height - 2 * v.height - margin)
                        yAnimationTheme.animateToFinalPosition((fabBackground.height - v.height).toFloat())
                        yAnimationThemeLayout.animateToFinalPosition((fabBackground.height - v.height).toFloat())
                    }

                    hasTouched = null
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (isFabMoved) {
                        setThemeFab()
                        isFabMoved = false
                    }
                    hasTouched = null
                }
            }
            true
        }

        favoriteFab.setOnTouchListener(mOnMovingTouchListener)
        gpsFab.setOnTouchListener(mOnMovingTouchListener)
        themeFab.setOnTouchListener(mOnMovingTouchListener)
    }
}