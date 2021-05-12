package com.jin.businfo_gumi.model.data

data class DataLiveItemForRoute(
    val stationId: String,
    val stationName: String,
    val routeOrder: Int?,
    val routeType: String,
    val vehicleNo: String,
    val lat: Double?,
    val lng: Double?
)