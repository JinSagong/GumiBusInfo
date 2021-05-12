package com.jin.businfo_gumi.ui.license.resources

import android.os.Bundle
import android.text.method.LinkMovementMethod
import com.jin.businfo_gumi.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_license_resources.*

class LicenseResourcesActivity : DaggerAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_resources)

        licenseResourcesBack.setOnClickListener { onBackPressed() }
        licenseResourcesContent.movementMethod = LinkMovementMethod.getInstance()
    }
}