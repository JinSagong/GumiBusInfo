package com.jin.businfo_gumi.model.repository

import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.util.Debug
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.create

object BusApi {
    private val apiKeyArray by lazy { arrayOf(appContext.getString(R.string.apiKey1)) }
    private var apiKeyIndex = 0
    private val cityCode by lazy { appContext.getString(R.string.CITY_CODE).toInt() }

    private val busForStationService by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(XmlConverterFactoryForStation.create())
            .baseUrl("http://openapi.tago.go.kr/openapi/service/")
            .build()
            .create<BusForStationService>()
    }
    private val busForRouteService by lazy {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(XmlConverterFactoryForRoute.create())
            .baseUrl("http://openapi.tago.go.kr/openapi/service/")
            .build()
            .create<BusForRouteService>()
    }

    /** 해당 정류장에 도착 예정인 버스들의 현재 위치 정보 */
    fun getCurrentInfoForStation(stationId: String) = busForStationService
        .getCurrentInfoForStation(apiKeyArray[apiKeyIndex], cityCode, stationId).single()

    /** 해당 노선의 버스들이 현재 위치한 정류장 정보 */
    fun getCurrentInfoForRoute(routeId: String) = busForRouteService
        .getCurrentInfoForRoute(apiKeyArray[apiKeyIndex], cityCode, routeId).single()

    private inline fun <reified T> Single<T>.single(): Single<T> {
        Debug.apiRequest(T::class.java.simpleName)
        return subscribeOn(Schedulers.io())
            .doOnSuccess { Debug.apiResponse(it.toString()) }
            .doOnError { Debug.apiError(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }
}