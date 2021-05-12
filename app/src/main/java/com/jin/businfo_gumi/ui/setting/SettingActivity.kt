package com.jin.businfo_gumi.ui.setting

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.ui.license.LicenseActivity
import com.jin.businfo_gumi.util.ReviewUtil
import com.jin.businfo_gumi.util.ThemeUtil
import com.jin.businfo_gumi.util.getIconAnimator
import com.jin.businfo_gumi.widget.Toasty
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*
import splitties.activities.start

class SettingActivity : DaggerAppCompatActivity() {
    private val iconAnimator by lazy { getIconAnimator(settingIcon) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        settingBack.setOnClickListener { onBackPressed() }

        settingGuide1.setOnClickListener { } // emptyListener
        settingGuide2.setOnClickListener { } // emptyListener
        settingGuide3.setOnClickListener { } // emptyListener

        settingKakao.setOnClickListener { viaKakao() }
        settingReview.setOnClickListener { ReviewUtil.openGooglePlay(this) }

        val versionText = "버전: ${packageManager.getPackageInfo(packageName, 0).versionName}"
        settingVersion.text = versionText

        settingLicense.text = SpannableString(settingLicense.text).apply {
            setSpan(UnderlineSpan(), 0, settingLicense.text.length, 0)
        }
        settingLicense.setOnClickListener { start<LicenseActivity>() }

        iconAnimator.start()
    }

    private fun viaKakao() {
        val kakaoIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.kakaoMessage))
            `package` = "com.kakao.talk"
        }
        try {
            startActivity(kakaoIntent)
        } catch (e: ActivityNotFoundException) {
            Toasty.alert(R.string.noKakao)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        iconAnimator.end()
    }
}