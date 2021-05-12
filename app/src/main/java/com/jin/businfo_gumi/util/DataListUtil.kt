package com.jin.businfo_gumi.util

import com.jin.businfo_gumi.MainApplication.Companion.dataPref
import com.jin.businfo_gumi.model.data.dataRouteList
import com.jin.businfo_gumi.model.data.dataStationList
import com.jin.businfo_gumi.ui.main.MainAdapter.Companion.viewTypeGpsStation
import com.jin.businfo_gumi.ui.main.MainAdapter.Companion.viewTypeRoute
import com.jin.businfo_gumi.ui.main.MainAdapter.Companion.viewTypeStation

@Suppress("UNUSED")
object DataListUtil {
    fun favorite() = dataPref.getFavorite().mapNotNull { favorite ->
        var result: Pair<Any, Int>? = null
        val route = dataRouteList.find { favorite == it.routeId }
        if (route != null) result = Pair(route, viewTypeRoute)
        else {
            val station = dataStationList.find { favorite == it.stationId }
            if (station != null) result = Pair(station, viewTypeStation)
        }
        result
    }

    fun gps(lat: Float, lng: Float, dR: Float) = dataStationList
        .filter {
            it.lat != null && it.lng != null && it.lat > (lat - dR) && it.lat < (lat + dR) && it.lng > (lng - dR) && it.lng < (lng + dR)
        }
        .map {
            Pair(
                Pair(it, LocationUtil.getDistance(it.lat!!, it.lng!!, lat, lng)), viewTypeGpsStation
            )
        }
        .sortedBy { it.first.second }
        .take(20)

    fun search(query: String, limit: Int = 20): List<Pair<Any, Int>> {
        val list1 = dataRouteList
            .filter { it.routeNo?.startsWith(query) == true }
            .sortedBy { it.routeNo?.length }
            .take(limit)
            .map { Pair(it, viewTypeRoute) }
        if (list1.size == limit) return list1

        val list2 = dataStationList
            .filter { it.stationName?.startsWith(query) == true }
            .sortedBy { it.stationName?.length }
            .take(limit - list1.size)
            .map { Pair(it, viewTypeStation) }
        if (list1.size + list2.size == limit) return list1 + list2

        val list3 = dataStationList
            .filter { it.stationNo?.startsWith(query) == true }
            .sortedBy { it.stationNo?.length }
            .take(limit - list1.size)
            .map { Pair(it, viewTypeStation) }
        return list1 + (list2 + list3).distinct()
    }

    fun history() = dataPref.getHistory().mapNotNull { history ->
        var result: Pair<Any, Int>? = null
        val route = dataRouteList.find { history == it.routeId }
        if (route != null) result = Pair(route, viewTypeRoute)
        else {
            val station = dataStationList.find { history == it.stationId }
            if (station != null) result = Pair(station, viewTypeStation)
        }
        result
    }

    fun recommend() = dataPref.getVisited()
        .filter { it !in dataPref.getFavorite() }
        .groupingBy { it }
        .eachCount()
        .map { Pair(it.key, it.value) }
        .reversed()
        .sortedByDescending { it.second }
        .mapNotNull { visited ->
            var result: Pair<Any, Int>? = null
            val route = dataRouteList.find { visited.first == it.routeId }
            if (route != null) result = Pair(route, viewTypeRoute)
            else {
                val station = dataStationList.find { visited.first == it.stationId }
                if (station != null) result = Pair(station, viewTypeStation)
            }
            result
        }

    fun <T> List<T>.getListByPage(page: Int) = take(getCountByPage(page))
    fun getCountByPage(page: Int) = page * 5 + 1
}