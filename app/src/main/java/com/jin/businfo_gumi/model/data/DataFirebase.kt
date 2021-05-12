package com.jin.businfo_gumi.model.data

import java.io.Serializable

data class DataRoute(
    val routeId: String? = null,
    val routeNo: String? = null,
    val routeType: String? = null,
    val startStation: String? = null,
    val endStation: String? = null
) : Serializable

data class DataStation(
    val stationId: String? = null,
    val stationName: String? = null,
    val stationNo: String? = null,
    val lat: Double? = null,
    val lng: Double? = null
) : Serializable

data class DataStopby(
    val routeId: String? = null,
    val order: Int? = null,
    val stationId: String? = null
)

val dataRouteList = ArrayList<DataRoute>()
val dataStationList = ArrayList<DataStation>()
val dataStopbyList = ArrayList<DataStopby>()