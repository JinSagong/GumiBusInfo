package com.jin.businfo_gumi.widget

import android.content.Context
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.util.Debug
import kotlinx.android.synthetic.main.dialog_msg_one_btn.*
import kotlinx.android.synthetic.main.dialog_msg_two_btn.*

@Suppress("UNUSED")
object MsgBottomSheetDialog {
    private var bottomSheetDialog: BottomSheetDialog? = null

    fun withOneBtn(context: Context): OneBtnBSDialog {
        bottomSheetDialog?.cancel()
        bottomSheetDialog = OneBtnBSDialog(context)
        return bottomSheetDialog!! as OneBtnBSDialog
    }

    fun withTwoBtn(context: Context): TwoBtnBSDialog {
        bottomSheetDialog?.cancel()
        bottomSheetDialog = TwoBtnBSDialog(context)
        return bottomSheetDialog!! as TwoBtnBSDialog
    }

    fun dismiss() = try {
        bottomSheetDialog?.dismiss()
    } catch (e: Exception) {
        Debug.i(e.message.toString())
    }

    class OneBtnBSDialog(context: Context) : BottomSheetDialog(context) {
        private var autoDismiss = true
        private var isDonePressed = false

        init {
            setContentView(R.layout.dialog_msg_one_btn)
            behavior.skipCollapsed = true
            setOnShowListener { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
            tv_dlg_msg1_done.setOnClickListener {
                isDonePressed = true
                if (autoDismiss) dismiss()
            }
        }

        fun autoDismiss(autoDismiss: Boolean = true) = apply { this.autoDismiss = autoDismiss }

        fun setMessage(msg: String) = apply { tv_dlg_msg1_description.text = msg }
        fun setMessage(resId: Int) =
            apply { tv_dlg_msg1_description.text = context.getString(resId) }

        fun setDoneText(text: String) = apply { tv_dlg_msg1_done.text = text }
        fun setDoneText(resId: Int) = apply { tv_dlg_msg1_done.text = context.getString(resId) }

        fun setOnDoneListener(l: (View) -> Unit) = apply {
            tv_dlg_msg1_done.setOnClickListener {
                isDonePressed = true
                l.invoke(it)
                if (autoDismiss) dismiss()
            }
        }

        fun setOnCancelListener(l: () -> Unit) = apply {
            setOnDismissListener { if (!isDonePressed) l.invoke() }
        }
    }

    class TwoBtnBSDialog(context: Context) : BottomSheetDialog(context) {
        private var autoDismiss = true
        private var isDonePressed = false

        init {
            setContentView(R.layout.dialog_msg_two_btn)
            behavior.skipCollapsed = true
            setOnShowListener { behavior.state = BottomSheetBehavior.STATE_EXPANDED }
            tv_dlg_msg2_cancel.setOnClickListener { dismiss() }
            tv_dlg_msg2_done.setOnClickListener {
                isDonePressed = true
                if (autoDismiss) dismiss()
            }
        }

        fun autoDismiss(autoDismiss: Boolean = true) = apply { this.autoDismiss = autoDismiss }

        fun setMessage(msg: String) = apply { tv_dlg_msg2_description.text = msg }
        fun setMessage(resId: Int) =
            apply { tv_dlg_msg2_description.text = context.getString(resId) }

        fun setDoneText(text: String) = apply { tv_dlg_msg2_done.text = text }
        fun setDoneText(resId: Int) = apply { tv_dlg_msg2_done.text = context.getString(resId) }

        fun setCancelText(text: String) = apply { tv_dlg_msg2_cancel.text = text }
        fun setCancelText(resId: Int) =
            apply { tv_dlg_msg2_cancel.text = context.getString(resId) }

        fun setOnDoneListener(l: (View) -> Unit) = apply {
            tv_dlg_msg2_done.setOnClickListener {
                isDonePressed = true
                l.invoke(it)
                if (autoDismiss) dismiss()
            }
        }

        fun setOnCancelListener(l: () -> Unit) = apply {
            setOnDismissListener { if (!isDonePressed) l.invoke() }
        }
    }
}