package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.model.data.DataLiveInfoForRoute
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BusForRouteService {
    /** 해당 노선의 버스들이 현재 위치한 정류장 정보 */
    @GET("BusLcInfoInqireService/getRouteAcctoBusLcList")
    fun getCurrentInfoForRoute(
        @Query("serviceKey", encoded = true) key: String,
        @Query("cityCode", encoded = true) cityCode: Int,
        @Query("routeId", encoded = true) routeId: String,
        @Query("numOfRows", encoded = true) numOfRows: Int = 1000,
        @Query("pageNo", encoded = true) pageNo: Int = 1
    ): Single<DataLiveInfoForRoute>
}