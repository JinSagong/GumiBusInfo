package com.jin.businfo_gumi.model.data

data class DataLiveItemForStation(
    val routeId: String,
    val routeNo: String,
    val arrTime: Int?,
    val prevStationCount: Int?,
    val routeType: String,
    val vehicleType: String
)