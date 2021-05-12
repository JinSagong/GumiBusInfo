package com.jin.businfo_gumi.model.pref.storage

import android.content.Context
import android.content.SharedPreferences
import com.jin.businfo_gumi.model.pref.util.IntPreference

@Suppress("UNUSED")
class ReviewPreferenceStorage(context: Context) {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences("review", Context.MODE_PRIVATE)
    }

    // do not need to reset
    var visitCount by IntPreference(prefs, "visitCount", 0)
    var visitCountWhenReviewShown by IntPreference(prefs, "visitCountWhenReviewShown", 0)
    val needToShowReviewApi get() = visitCount - visitCountWhenReviewShown >= 50
}