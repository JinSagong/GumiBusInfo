package com.jin.businfo_gumi.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.tasks.RuntimeExecutionException
import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.MainApplication.Companion.reviewPref
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.widget.Toasty

object ReviewUtil {
    private val manager by lazy { ReviewManagerFactory.create(appContext) }

    fun launchApi(activity: Activity) {
        manager.requestReviewFlow()
            .addOnCompleteListener { task1 ->
                if (task1.isSuccessful) {
                    manager.launchReviewFlow(activity, task1.result)
                        .addOnCompleteListener {
                            reviewPref.visitCountWhenReviewShown = reviewPref.visitCount
                        }
                } else {
                    try {
                        val exception = (task1.exception as RuntimeExecutionException?)
                        @ReviewErrorCode val errorCode = exception?.errorCode
                        Debug.e("fail to request review flow: [$errorCode]${exception?.message}")
                    } catch (e: Exception) {
                        Debug.e("fail to request review flow: [null]${e.message}")
                    }
                }
            }
    }

    fun openGooglePlay(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.jin.businfo_gumi")
            `package` = "com.android.vending"
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toasty.alert(R.string.noGooglePlay)
        }
    }
}