package com.jin.businfo_gumi.model.data

data class DataLiveInfoForStation(
    val resultCode: String,
    val resultMsg: String,
    val items: List<DataLiveItemForStation>
)