package com.jin.businfo_gumi.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.MainApplication.Companion.dataPref
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.*
import com.jin.businfo_gumi.util.Network
import com.jin.businfo_gumi.util.readSingleItem
import javax.inject.Inject

class FirebaseViewModel @Inject constructor() : ViewModel() {
    private val database =
        Firebase.database.reference.child(appContext.getString(R.string.CITY_CODE))
    private val refVersion = database.child("version")
    private val refRoute = database.child("route")
    private val refStation = database.child("station")
    private val refStopby = database.child("stopby")
    private val refNoticeVersion = database.child("noticeVersion")
    private val refNoticeMessage = database.child("noticeMessage")

    fun getDataVersion(callback: ((Int) -> Unit)?) {
        refVersion.readSingleItem {
            val version = it.getValue(Int::class.java)
            if (version != null) callback?.invoke(version)
        }
    }

    fun getFirebaseData(callback: (() -> Unit)?) {
        var finishCount = 0
        refRoute.readSingleItem {
            val route = it.children.mapNotNull { child -> child.getValue(DataRoute::class.java) }
            dataRouteList.clear()
            dataRouteList.addAll(route)
            dataPref.setDataRoute(route)
            synchronized(this) {
                finishCount++
                if (finishCount == 3) callback?.invoke()
            }
        }
        refStation.readSingleItem {
            val station = it.children.mapNotNull { child ->
                DataStation(
                    child.child("stationId").getValue(String::class.java),
                    child.child("stationName").getValue(String::class.java),
                    child.child("stationNo").getValue(String::class.java),
                    child.child("lat").getValue(String::class.java)?.toDoubleOrNull(),
                    child.child("lng").getValue(String::class.java)?.toDoubleOrNull()
                )
            }
            dataStationList.clear()
            dataStationList.addAll(station)
            dataPref.setDataStation(station)
            synchronized(this) {
                finishCount++
                if (finishCount == 3) callback?.invoke()
            }
        }
        refStopby.readSingleItem {
            val stopby = it.children.mapNotNull { child ->
                DataStopby(
                    child.child("routeId").getValue(String::class.java),
                    child.child("order").getValue(String::class.java)?.toIntOrNull(),
                    child.child("stationId").getValue(String::class.java)
                )
            }
            dataStopbyList.clear()
            dataStopbyList.addAll(stopby)
            dataPref.setDataStopby(stopby)
            synchronized(this) {
                finishCount++
                if (finishCount == 3) callback?.invoke()
            }
        }
    }

    fun getNotice(callback: ((String, Int) -> Unit)? = null) {
        if (!Network.checkConnection(false)) return
        refNoticeVersion.readSingleItem { nv ->
            val version = nv.getValue(Int::class.java) ?: 0
            if (version > dataPref.noticeVersion) refNoticeMessage.readSingleItem { nm ->
                val msg = nm.getValue(String::class.java)?.trim()
                if (!msg.isNullOrEmpty()) callback?.invoke(msg, version)
            }
        }
    }
}