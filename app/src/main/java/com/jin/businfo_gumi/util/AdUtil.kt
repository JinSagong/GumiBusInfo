package com.jin.businfo_gumi.util

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jin.businfo_gumi.MainApplication.Companion.appContext

@Suppress("UNUSED")
object AdUtil {
    private lateinit var adRequest: AdRequest

    fun init(context: Context) {
        MobileAds.initialize(context)
        adRequest = AdRequest.Builder().build()
    }

    fun load(adView: AdView) {
        adView.loadAd(adRequest)
    }

    val heightBannerDp = AdSize.BANNER.height
    val heightBannerPx = AdSize.BANNER.getHeightInPixels(appContext)
    val widthBannerDp = AdSize.BANNER.width
    val widthBannerPx = AdSize.BANNER.getWidthInPixels(appContext)

    val heightLargeBannerDp = AdSize.LARGE_BANNER.height
    val heightLargeBannerPx = AdSize.LARGE_BANNER.getHeightInPixels(appContext)
    val widthLargeBannerDp = AdSize.LARGE_BANNER.width
    val widthLargeBannerPx = AdSize.LARGE_BANNER.getWidthInPixels(appContext)

    val heightMediumRectangleDp = AdSize.MEDIUM_RECTANGLE.height
    val heightMediumRectanglePx = AdSize.MEDIUM_RECTANGLE.getHeightInPixels(appContext)
    val widthMediumRectangleDp = AdSize.MEDIUM_RECTANGLE.width
    val widthMediumRectanglePx = AdSize.MEDIUM_RECTANGLE.getWidthInPixels(appContext)
}