package com.jin.businfo_gumi.model.pref.storage

import android.content.Context
import android.content.SharedPreferences
import com.jin.businfo_gumi.model.data.DataRoute
import com.jin.businfo_gumi.model.data.DataStation
import com.jin.businfo_gumi.model.data.DataStopby
import com.jin.businfo_gumi.model.pref.util.IntPreference
import com.jin.businfo_gumi.model.pref.util.StringPreference

@Suppress("UNUSED")
class DataPreferenceStorage(context: Context) {
    private val prefs: Lazy<SharedPreferences> = lazy {
        context.applicationContext.getSharedPreferences("data", Context.MODE_PRIVATE)
    }

    /** Notice Version */

    var noticeVersion by IntPreference(prefs, "noticeVersion", 0)

    /** Data Version */

    var dataVersion by IntPreference(prefs, "dataVersion", 0)


    /** Data */

    private var dataRoute by StringPreference(prefs, "dataRoute")

    fun setDataRoute(list: List<DataRoute>) {
        dataRoute =
            list.joinToString(REGEX1) { it.routeId + REGEX2 + it.routeNo + REGEX3 + it.routeType + REGEX4 + it.startStation + REGEX5 + it.endStation }
    }

    fun getDataRoute() = dataRoute?.split(REGEX1)?.map {
        val routeId = it.split(REGEX2).firstOrNull()?.ifEmpty { null }
        val routeNo = it.substringAfter(REGEX2).split(REGEX3).firstOrNull()?.ifEmpty { null }
        val routeType = it.substringAfter(REGEX3).split(REGEX4).firstOrNull()?.ifEmpty { null }
        val startStation = it.substringAfter(REGEX4).split(REGEX5).firstOrNull()?.ifEmpty { null }
        val endStation = it.substringAfter(REGEX5).ifEmpty { null }
        DataRoute(routeId, routeNo, routeType, startStation, endStation)
    }.orEmpty()

    private var dataStation by StringPreference(prefs, "dataStation")

    fun setDataStation(list: List<DataStation>) {
        dataStation =
            list.joinToString(REGEX1) { it.stationId + REGEX2 + it.stationName + REGEX3 + it.stationNo + REGEX4 + it.lat + REGEX5 + it.lng }
    }

    fun getDataStation() = dataStation?.split(REGEX1)?.map {
        val stationId = it.split(REGEX2).firstOrNull()?.ifEmpty { null }
        val stationName = it.substringAfter(REGEX2).split(REGEX3).firstOrNull()?.ifEmpty { null }
        val stationNo = it.substringAfter(REGEX3).split(REGEX4).firstOrNull()?.ifEmpty { null }
        val lat = it.substringAfter(REGEX4).split(REGEX5).firstOrNull()?.ifEmpty { null }
            ?.toDoubleOrNull()
        val lng = it.substringAfter(REGEX5).ifEmpty { null }?.toDoubleOrNull()
        DataStation(stationId, stationName, stationNo, lat, lng)
    }.orEmpty()

    private var dataStopby by StringPreference(prefs, "dataStopby")

    fun setDataStopby(list: List<DataStopby>) {
        dataStopby =
            list.joinToString(REGEX1) { it.routeId + REGEX2 + it.order + REGEX3 + it.stationId }
    }

    fun getDataStopby() = dataStopby?.split(REGEX1)?.map {
        val routeId = it.split(REGEX2).firstOrNull()?.ifEmpty { null }
        val order =
            it.substringAfter(REGEX2).split(REGEX3).firstOrNull()?.ifEmpty { null }?.toIntOrNull()
        val stationId = it.substringAfter(REGEX3).ifEmpty { null }
        DataStopby(routeId, order, stationId)
    }.orEmpty()


    /** Favorite */

    private var favorite by StringPreference(prefs, "favorite")

    @Synchronized
    fun addFavorite(id: String?) {
        if (id == null) return
        removeFavorite(id)
        favorite = if (favorite == null) id else id + REGEX1 + favorite
    }

    @Synchronized
    fun removeFavorite(id: String?) {
        if (id == null) return
        favorite = favorite
            ?.split(REGEX1)
            ?.toCollection(ArrayList())
            ?.apply { remove(id) }
            ?.joinToString(REGEX1)
    }

    @Synchronized
    fun getFavorite() = favorite?.split(REGEX1).orEmpty()


    /** History */

    private var history by StringPreference(prefs, "history")

    @Synchronized
    fun addHistory(id: String?) {
        if (id == null) return
        removeHistory(id)
        history = if (history == null) id else id + REGEX1 + history
    }

    @Synchronized
    fun removeHistory(id: String?) {
        if (id == null) return
        history = history
            ?.split(REGEX1)
            ?.toCollection(ArrayList())
            ?.apply { remove(id) }
            ?.joinToString(REGEX1)
    }

    @Synchronized
    fun clearHistory() {
        history = null
    }

    @Synchronized
    fun getHistory() = history?.split(REGEX1).orEmpty()


    /** Visited */

    private var visited by StringPreference(prefs, "visited")

    @Synchronized
    fun addVisited(id: String?) {
        if (id == null) return

        visited = if (visited == null) id else visited + REGEX1 + id
    }

    @Synchronized
    fun getVisited() = visited?.split(REGEX1).orEmpty()

    fun reset() {
        dataVersion = 0
        dataRoute = null
        dataStation = null
        dataStopby = null
        favorite = null
        history = null
        visited = null
    }

    companion object {
        private const val REGEX1 = "#^%$"
        private const val REGEX2 = "&&@!"
        private const val REGEX3 = "**&^"
        private const val REGEX4 = "&%^@"
        private const val REGEX5 = "!~@~"
    }
}