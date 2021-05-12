package com.jin.businfo_gumi.ui.info

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.jin.businfo_gumi.MainApplication.Companion.dataPref
import com.jin.businfo_gumi.MainApplication.Companion.reviewPref
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.*
import com.jin.businfo_gumi.ui.info.InfoAdapter.Companion.viewTypeForRoute
import com.jin.businfo_gumi.ui.info.InfoAdapter.Companion.viewTypeForStation
import com.jin.businfo_gumi.util.*
import com.jin.businfo_gumi.viewmodel.BusViewModel
import com.jin.businfo_gumi.widget.Toasty
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_info.*
import javax.inject.Inject

class InfoActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val busViewModel by lazy { getViewModel<BusViewModel>(viewModelFactory) }

    private val dataRoute by lazy { intent.getSerializableExtra("dataRoute") as DataRoute? }
    private val dataStation by lazy { intent.getSerializableExtra("dataStation") as DataStation? }
    private val isRouteInfo by lazy { dataRoute != null }

    private val adapter by lazy { InfoAdapter(this) }
    private val listForRoute by lazy {
        dataStopbyList.filter { it.routeId == dataRoute?.routeId }.sortedBy { it.order }
            .mapNotNull { dataStationList.firstOrNull { s -> it.stationId == s.stationId } }
            .also {
                runOnUiThread {
                    if (it.isEmpty()) infoEmptySignView.toggle(true)
                    else infoRefreshFab.visibility = View.VISIBLE
                }
            }
    }
    private val listForStation by lazy {
        dataStopbyList.filter { it.stationId == dataStation?.stationId }
            .mapNotNull { dataRouteList.firstOrNull { r -> r.routeId == it.routeId } }
            .sortedBy { it.routeNo }
            .also {
                runOnUiThread {
                    if (it.isEmpty()) infoEmptySignView.toggle(true)
                    else infoRefreshFab.visibility = View.VISIBLE
                }
            }
    }

    private val typedValue = TypedValue()

    private var refreshable = false
    private var hasLoaded = false

    private val idxListForRoute = arrayListOf<Int>()
    private var idxForRoute = 0

    private val mapHeight by lazy { infoMapContainer.height.toFloat() }
    private val navigationHeight by lazy { infoNavigation.height.toFloat() }
    private val statusBarHeight by lazy { infoStatusBar.height.toFloat() }
    private val blankTop by lazy { resources.getDimensionPixelSize(R.dimen.dp32) }
    private val collapsingMapHeight by lazy { mapHeight - statusBarHeight - navigationHeight - blankTop }
    private val collapsingHeight by lazy { mapHeight - statusBarHeight - blankTop }
    private val mapCoverElevation by lazy { infoMapBottomCover.elevation }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        StatusBar.setTransparentStatusBar(this)
        StatusBar.setHeightOfStatusBar(this, infoStatusBar)

        reviewPref.visitCount++

        infoBack.setOnClickListener { onBackPressed() }

        init()
        if (isRouteInfo) setRouteInfo() else setStationInfo()
        setMap()
        setCollapsingEvent()
    }

    private fun init() {
        theme.resolveAttribute(R.attr.themeColor, typedValue, true)

        infoRecyclerView.adapter = adapter
        infoRecyclerView.layoutManager = LinearLayoutManager(this)

        infoRefreshFab.setOnClickListener {
            if (!refreshable) return@setOnClickListener
            if (isRouteInfo) refreshForRoute() else refreshForStation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setRouteInfo() {
        dataPref.addHistory(dataRoute?.routeId)
        dataPref.addVisited(dataRoute?.routeId)

        var isFavorite = dataPref.getFavorite().contains(dataRoute?.routeId)
        infoFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp)
        infoFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) dataPref.addFavorite(dataRoute?.routeId)
            else dataPref.removeFavorite(dataRoute?.routeId)
            infoFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp)
        }

        infoTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24f)
        infoTitleTextView.text = dataRoute?.routeNo
        infoDescriptionTextView1.text = "기점: ${dataRoute?.startStation}"
        infoDescriptionTextView2.text = "종점: ${dataRoute?.endStation}"
        infoDescriptionTextView2.visibility = View.VISIBLE

        val typePair = getTypePair(this, dataRoute?.routeType)
        infoTypeTextView.text = typePair.first
        infoTypeFakeTextView.text = typePair.first
        infoTypeTextView.backgroundTintList = typePair.second
        infoTypeTextView.visibility = View.VISIBLE
        infoTypeFakeTextView.visibility = View.INVISIBLE

        infoGuide.text = getString(R.string.loadingRouteInfo)

        infoBusFab.setOnClickListener {
            val itemY = infoRecyclerView.getChildAt(idxListForRoute[idxForRoute]).y
            val toY = (itemY + collapsingHeight).toInt()
            infoContent.requestFocus()
            infoContent.smoothScrollTo(0, toY)
            idxForRoute++
            if (idxForRoute >= idxListForRoute.size) idxForRoute = 0
        }

        Thread {
            val list = listForRoute.map { Pair(it, null) }
            runOnUiThread {
                adapter.updateWithSingleViewType(list, viewTypeForRoute)
                refreshForRoute()
            }
        }.start()
    }

    private fun refreshForRoute() {
        setRefreshable(false)
        busViewModel.getCurrentInfoForRoute(dataRoute?.routeId) { data, errorMsg ->
            if (data == null) {
                infoGuide.text = errorMsg
                infoGuide.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                infoBusFab.visibility = View.GONE
                adapter.updateWithSingleViewType(
                    listForRoute.map { Pair(it, null) },
                    viewTypeForRoute
                )
                setRefreshable(true, isSuccess = false, msg = errorMsg)
            } else {
                val numOfBus = data.items.size
                infoGuide.text =
                    if (numOfBus != 0) "현재 ${numOfBus}대의 버스가 운행중입니다" else getString(R.string.noRouteInfo)
                infoGuide.setTextColor(
                    ContextCompat.getColor(
                        this, if (numOfBus == 0) R.color.text_gray else typedValue.resourceId
                    )
                )

                infoBusFab.visibility = if (numOfBus != 0) View.VISIBLE else View.GONE
                idxListForRoute.clear()
                idxForRoute = 0
                val list = listForRoute.mapIndexed { idx, r ->
                    val vehicleList = data.items.filter { d -> d.stationId == r.stationId }
                    if (vehicleList.isNotEmpty()) idxListForRoute.add(idx)
                    Pair(r, vehicleList)
                }
                adapter.updateWithSingleViewType(list, viewTypeForRoute)
                setRefreshable(true, isSuccess = true)
            }
        }
    }

    private fun setStationInfo() {
        dataPref.addHistory(dataStation?.stationId)
        dataPref.addVisited(dataStation?.stationId)

        var isFavorite = dataPref.getFavorite().contains(dataStation?.stationId)
        infoFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp)
        infoFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) dataPref.addFavorite(dataStation?.stationId)
            else dataPref.removeFavorite(dataStation?.stationId)
            infoFavorite.setImageResource(if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp)
        }

        infoTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22f)
        infoTitleTextView.text = dataStation?.stationName
        infoDescriptionTextView1.text = dataStation?.stationNo

        infoGuide.text = getString(R.string.loadingStationInfo)

        Thread {
            val list = listForStation.map { Triple(it, null, null) }
            runOnUiThread {
                adapter.updateWithSingleViewType(list, viewTypeForStation)
                refreshForStation()
            }
        }.start()
    }

    private fun refreshForStation() {
        setRefreshable(false)
        busViewModel.getCurrentInfoForStation(dataStation?.stationId) { data, errorMsg ->
            if (data == null) {
                infoGuide.text = errorMsg
                infoGuide.setTextColor(ContextCompat.getColor(this, R.color.text_gray))
                adapter.updateWithSingleViewType(
                    listForStation.map { Triple(it, null, null) }, viewTypeForStation
                )
                setRefreshable(true, isSuccess = false, msg = errorMsg)
            } else {
                val numOfArrival = data.items.size
                infoGuide.text =
                    if (numOfArrival != 0) "${numOfArrival}개 노선의 버스가 도착할 예정입니다" else getString(R.string.noStationInfo)
                infoGuide.setTextColor(
                    ContextCompat.getColor(
                        this, if (numOfArrival == 0) R.color.text_gray else typedValue.resourceId
                    )
                )
                val list = listForStation.map {
                    val liveInfo = data.items.firstOrNull { d -> d.routeId == it.routeId }
                    var sName: String? = null
                    if (liveInfo != null) {
                        val order =
                            dataStopbyList.firstOrNull { sb -> sb.routeId == it.routeId && sb.stationId == dataStation?.stationId }?.order
                        if (order != null && liveInfo.prevStationCount != null) {
                            val idx = order - liveInfo.prevStationCount
                            val sId =
                                dataStopbyList.firstOrNull { sb -> sb.routeId == it.routeId && sb.order == idx }?.stationId
                            if (sId != null) sName =
                                dataStationList.firstOrNull { s -> s.stationId == sId }?.stationName
                        }
                    }
                    Triple(it, liveInfo, sName)
                }.sortedBy { it.second?.arrTime ?: Int.MAX_VALUE }
                adapter.updateWithSingleViewType(list, viewTypeForStation)
                setRefreshable(true, isSuccess = true)
            }
        }
    }

    private fun setRefreshable(enable: Boolean, isSuccess: Boolean = true, msg: String? = null) =
        infoRecyclerView.post {
            refreshable = enable
            infoRefreshFab.alpha = if (enable) 1f else 0.5f
            if (enable) {
                if (isSuccess) {
                    if (hasLoaded) Toasty.msg(R.string.dataLoadingSuccess) else hasLoaded = true
                } else {
                    if (msg != getString(R.string.networkError)) Toasty.alert(R.string.noResultBecauseOfError)
                    if (!hasLoaded) hasLoaded = true
                }
            }
        }

    private fun setMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.infoMap) as SupportMapFragment
        mapFragment.getMapAsync {
            if (isRouteInfo) {
                dataStopbyList
                    .filter { d -> d.routeId == dataRoute?.routeId }
                    .sortedBy { d -> d.order }
                    .let { list ->
                        var sumLat = 0.0
                        var sumLng = 0.0
                        var count = 0
                        val themeColorAttribute =
                            obtainStyledAttributes(intArrayOf(R.attr.themeColor))
                        it.addPolyline(
                            PolylineOptions()
                                .color(themeColorAttribute.getColorOrThrow(0))
                                .addAll(list.mapNotNull { d ->
                                    val station =
                                        dataStationList.firstOrNull { dd -> dd.stationId == d.stationId }
                                    val lat = station?.lat
                                    val lng = station?.lng

                                    if (lat != null && lng != null) {
                                        sumLat += lat
                                        sumLng += lng
                                        count++
                                        LatLng(lat, lng)
                                    } else null
                                })
                        )
                        themeColorAttribute.recycle()
                        it.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                if (count == 0) LatLng(36.12845819999403, 128.3306798846628) // 구미역
                                else LatLng(sumLat / count, sumLng / count), 12f
                            )
                        )
                    }
            } else {
                val lat = dataStation?.lat
                val lng = dataStation?.lng
                if (lat != null && lng != null) {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 16f))
                    it.addMarker(MarkerOptions().position(LatLng(lat, lng)))
                } else {
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(36.12845819999403, 128.3306798846628), 16f // 구미역
                        )
                    )
                }
            }
        }

        if (isRouteInfo) infoMapRouteWarning.visibility = View.VISIBLE
    }

    private fun setCollapsingEvent() {
        infoMapCover.setOnClickListener(null)
        infoMapBottomCover.setOnClickListener(null)
        infoContentTitle.setOnClickListener(null)

        var mapArea = 0
        infoContent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            infoMapContainer.translationY = scrollY.toFloat()
                .let { if (it < collapsingHeight) -it else -collapsingHeight } / 2
            infoMapRouteWarning.alpha =
                ((navigationHeight - scrollY) / navigationHeight).let { if (it <= 0f) 0f else if (it >= 1f) 1f else it }
            infoMapCover.visibility =
                if (scrollY < collapsingMapHeight * 2 / 3) View.GONE else View.VISIBLE
            infoMapCover.alpha =
                ((scrollY - collapsingMapHeight * 2 / 3) / (collapsingMapHeight / 3)).let { if (it <= 0f) 0f else if (it >= 1f) 1f else it }
            infoMapBottomCover.elevation =
                (scrollY - collapsingMapHeight).let { if (it < 0) mapCoverElevation else if (it > navigationHeight) 0f else mapCoverElevation * (1f - it / navigationHeight) }
            if (scrollY <= collapsingHeight || mapArea != (mapHeight + statusBarHeight + blankTop).toInt() / 2 + 1)
                infoMapContainer.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    mapArea =
                        (mapHeight.toInt() - scrollY / 2).let { if (it < (mapHeight + statusBarHeight + blankTop) / 2 + 1) (mapHeight + statusBarHeight + blankTop).toInt() / 2 + 1 else it }
                    height = mapArea
                }
            infoContentTitle.translationY = scrollY.toFloat()
                .let { if (it < collapsingHeight) 0f else it - collapsingHeight }
        })

        infoContent.setOnScrollStateListener { view, y ->
            if (y < collapsingHeight / 2) view.smoothScrollTo(0, 0)
            else if (y >= collapsingHeight / 2 && y < collapsingHeight)
                view.smoothScrollTo(0, collapsingHeight.toInt())
        }
    }
}