package com.jin.businfo_gumi.viewmodel

import androidx.lifecycle.ViewModel
import com.jin.businfo_gumi.MainApplication.Companion.appContext
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.DataLiveInfoForRoute
import com.jin.businfo_gumi.model.data.DataLiveInfoForStation
import com.jin.businfo_gumi.model.repository.BusApi
import com.jin.businfo_gumi.util.Network
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class BusViewModel @Inject constructor() : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    fun getCurrentInfoForRoute(
        routeId: String?,
        callback: ((DataLiveInfoForRoute?, String?) -> Unit)? = null
    ) {
        if (routeId == null) {
            callback?.invoke(null, appContext.getString(R.string.noResultBecauseOfError))
            return
        }
        if (!Network.checkConnection()) {
            callback?.invoke(null, appContext.getString(R.string.networkError))
            return
        }
        compositeDisposable.add(
            BusApi.getCurrentInfoForRoute(routeId)
                .subscribe({ callback?.invoke(it, null) }, { callback?.invoke(null, it.message) })
        )
    }

    fun getCurrentInfoForStation(
        stationId: String?,
        callback: ((DataLiveInfoForStation?, String?) -> Unit)? = null
    ) {
        if (stationId == null) {
            callback?.invoke(null, appContext.getString(R.string.noResultBecauseOfError))
            return
        }
        if (!Network.checkConnection()) {
            callback?.invoke(null, appContext.getString(R.string.networkError))
            return
        }
        compositeDisposable.add(
            BusApi.getCurrentInfoForStation(stationId)
                .subscribe({ callback?.invoke(it, null) }, { callback?.invoke(null, it.message) })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}