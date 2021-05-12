package com.jin.businfo_gumi.ui.license

import android.os.Bundle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.ui.license.resources.LicenseResourcesActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_license.*
import splitties.activities.start

class LicenseActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        licenseBack.setOnClickListener { onBackPressed() }

        licenseAOS.setOnClickListener { start<OssLicensesMenuActivity>() }
        licenseResources.setOnClickListener { start<LicenseResourcesActivity>() }
    }
}