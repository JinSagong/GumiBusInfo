package com.jin.businfo_gumi.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.ui.main.MainAdapter
import com.jin.businfo_gumi.util.getIconAnimator
import kotlinx.android.synthetic.main.layout_empty_sign_view.view.*

@Suppress("UNUSED")
class EmptySignView : FrameLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    private lateinit var view: View
    private val iconAnimator by lazy { getIconAnimator(view) }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        view = View.inflate(context, R.layout.layout_empty_sign_view, null)
        addView(view)

        visibility = View.GONE

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmptySignView, 0, 0)
            try {
                view.emptySignTextView.text =
                    typedArray.getString(R.styleable.EmptySignView_emptySignText)
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setText(text: String) {
        view.emptySignTextView.text = text
    }

    fun setText(@StringRes textId: Int) {
        view.emptySignTextView.text = context.getString(textId)
    }

    fun toggle(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
        if (visible) {
            if (!iconAnimator.isRunning) iconAnimator.start()
        } else {
            if (iconAnimator.isRunning) iconAnimator.end()
        }
    }

    fun mainToggle(adapter: MainAdapter, type: String) {
        val isEmpty = adapter.itemCount == 0
        if (isEmpty) {
            when (type) {
                TYPE_FAVORITE -> setText(R.string.initPageMsg)
                TYPE_GPS_PREPARED -> setText(R.string.loadingGpsInfo)
                TYPE_GPS_FAILED -> setText(R.string.noGpsInfo)
            }
        }
        toggle(isEmpty)
    }

    fun updateTheme() {
        view.emptySignImageView.setImageDrawable(
            VectorDrawableCompat.create(context.resources, R.drawable.ic_bus_48dp, context.theme)
        )
    }

    companion object {
        const val TYPE_FAVORITE = "favorite"
        const val TYPE_GPS_PREPARED = "gpsPrepared"
        const val TYPE_GPS_FAILED = "gpsFailed"
    }
}