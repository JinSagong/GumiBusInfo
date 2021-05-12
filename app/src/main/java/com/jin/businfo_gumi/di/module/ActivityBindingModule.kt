package com.jin.businfo_gumi.di.module

import com.jin.businfo_gumi.di.annotation.ActivityScope
import com.jin.businfo_gumi.ui.info.InfoActivity
import com.jin.businfo_gumi.ui.info.InfoModule
import com.jin.businfo_gumi.ui.license.LicenseActivity
import com.jin.businfo_gumi.ui.license.resources.LicenseResourcesActivity
import com.jin.businfo_gumi.ui.main.MainActivity
import com.jin.businfo_gumi.ui.main.MainModule
import com.jin.businfo_gumi.ui.setting.SettingActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [InfoModule::class])
    internal abstract fun infoActivity(): InfoActivity

    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun settingActivity(): SettingActivity

    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun licenseActivity(): LicenseActivity

    @ActivityScope
    @ContributesAndroidInjector
    internal abstract fun licenseResourcesActivity(): LicenseResourcesActivity
}