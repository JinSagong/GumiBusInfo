package com.jin.businfo_gumi.widget

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.jin.businfo_gumi.util.Debug

@Suppress("UNUSED")
class StateScrollView : NestedScrollView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int)
            : super(context, attrs, defStyle)

    private var mScrollStateListener: OnScrollStateListener? = null
    private var scrollState = -1

    fun setOnScrollStateListener(l: OnScrollStateListener?) {
        mScrollStateListener = l
    }

    fun setOnScrollStateListener(l: ((NestedScrollView, Int) -> Unit)?) {
        mScrollStateListener = object : OnScrollStateListener {
            override fun onScrollStateIdle(view: NestedScrollView, y: Int) {
                l?.invoke(view, y)
            }
        }
    }

    @Synchronized
    private fun setScrollState(d: Int) {
        if (scrollState < 0 && d == 1) scrollState = 1
        else scrollState += d
        Debug.i("scrollState = $scrollState")
        if (scrollState == -1) mScrollStateListener?.onScrollStateIdle(this, scrollY)
    }

    override fun stopNestedScroll(type: Int) {
        super.stopNestedScroll(type)
        setScrollState(-1)
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        setScrollState(1)
        return super.startNestedScroll(axes, type)
    }

    interface OnScrollStateListener {
        fun onScrollStateIdle(view: NestedScrollView, y: Int)
    }
}