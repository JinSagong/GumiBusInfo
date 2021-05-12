package com.jin.businfo_gumi.widget

import android.app.Activity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.updateLayoutParams
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.util.AdUtil
import kotlinx.android.synthetic.main.dialog_close.*

class CloseDialog(activity: Activity) :
    AppCompatDialog(activity) {
    init {
        setContentView(R.layout.dialog_close)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCanceledOnTouchOutside(false)

        bgAdView3.updateLayoutParams<FrameLayout.LayoutParams> {
            width = AdUtil.widthMediumRectanglePx
            height = AdUtil.heightMediumRectanglePx
        }

        var canFinish = true
        closeCancel.setOnClickListener {
            canFinish = false
            dismiss()
        }
        closeConfirm.setOnClickListener { dismiss() }
        setOnDismissListener { if (canFinish) activity.finish() }
    }

    override fun show() {
        super.show()
        AdUtil.load(adView3)
    }
}