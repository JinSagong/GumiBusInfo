package com.jin.businfo_gumi.model.data

data class DataLiveInfoForRoute(
    val resultCode: String,
    val resultMsg: String,
    val items: List<DataLiveItemForRoute>
)