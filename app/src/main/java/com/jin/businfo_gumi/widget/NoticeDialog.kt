package com.jin.businfo_gumi.widget

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.util.getIconAnimator
import kotlinx.android.synthetic.main.dialog_notice.*

class NoticeDialog(context: Context, msg: String, doOnDismiss: (() -> Unit)? = null) :
    AppCompatDialog(context) {
    private val iconAnimator by lazy { getIconAnimator(noticeIcon) }

    init {
        setContentView(R.layout.dialog_notice)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCanceledOnTouchOutside(false)
        noticeMessage.text = msg
        noticeConfirm.setOnClickListener { dismiss() }
        setOnDismissListener {
            iconAnimator.end()
            doOnDismiss?.invoke()
        }
    }

    override fun show() {
        super.show()
        iconAnimator.start()
    }
}