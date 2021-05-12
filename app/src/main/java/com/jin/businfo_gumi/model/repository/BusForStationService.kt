package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForStation
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BusForStationService {
    /** 해당 정류장에 도착 예정인 버스들의 현재 위치 정보 */
    @GET("ArvlInfoInqireService/getSttnAcctoArvlPrearngeInfoList")
    fun getCurrentInfoForStation(
        @Query("serviceKey", encoded = true) key: String,
        @Query("cityCode", encoded = true) cityCode: Int,
        @Query("nodeId", encoded = true) stationId: String,
        @Query("numOfRows", encoded = true) numOfRows: Int = 1000,
        @Query("pageNo", encoded = true) pageNo: Int = 1
    ): Single<DataLiveInfoForStation>
}