package com.jin.businfo_gumi

import android.content.Context
import android.graphics.Typeface
import com.jin.businfo_gumi.di.DaggerAppComponent
import com.jin.businfo_gumi.model.data.dataRouteList
import com.jin.businfo_gumi.model.data.dataStationList
import com.jin.businfo_gumi.model.data.dataStopbyList
import com.jin.businfo_gumi.model.pref.storage.DataPreferenceStorage
import com.jin.businfo_gumi.model.pref.storage.ReviewPreferenceStorage
import com.jin.businfo_gumi.model.pref.storage.SettingPreferenceStorage
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MainApplication : DaggerApplication() {
    companion object {
        lateinit var appContext: Context
        lateinit var settingPref: SettingPreferenceStorage
        lateinit var dataPref: DataPreferenceStorage
        lateinit var reviewPref: ReviewPreferenceStorage
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        settingPref = SettingPreferenceStorage(applicationContext)
        dataPref = DataPreferenceStorage(applicationContext)
        reviewPref = ReviewPreferenceStorage(applicationContext)

        dataRouteList.addAll(dataPref.getDataRoute())
        dataStationList.addAll(dataPref.getDataStation())
        dataStopbyList.addAll(dataPref.getDataStopby())

        setDefaultFont("MONOSPACE", "nanum.ttf")
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    @Suppress("SameParameterValue")
    private fun setDefaultFont(staticTypefaceFieldName: String, fontAssetName: String) {
        val regular = Typeface.createFromAsset(assets, fontAssetName)
        try {
            val staticField = Typeface::class.java.getDeclaredField(staticTypefaceFieldName)
            staticField.isAccessible = true
            staticField[null] = regular
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}
