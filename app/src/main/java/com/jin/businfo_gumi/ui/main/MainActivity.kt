package com.jin.businfo_gumi.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.jin.businfo_gumi.MainApplication.Companion.dataPref
import com.jin.businfo_gumi.MainApplication.Companion.reviewPref
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.dataRouteList
import com.jin.businfo_gumi.model.data.dataStationList
import com.jin.businfo_gumi.model.data.dataStopbyList
import com.jin.businfo_gumi.ui.setting.SettingActivity
import com.jin.businfo_gumi.util.*
import com.jin.businfo_gumi.util.DataListUtil.getListByPage
import com.jin.businfo_gumi.util.LocationUtil.Companion.REQUEST_LOCATION
import com.jin.businfo_gumi.util.LocationUtil.Companion.RESPONSE_LOCATION_CANCEL
import com.jin.businfo_gumi.viewmodel.FirebaseViewModel
import com.jin.businfo_gumi.widget.CloseDialog
import com.jin.businfo_gumi.widget.EmptySignView.Companion.TYPE_FAVORITE
import com.jin.businfo_gumi.widget.EmptySignView.Companion.TYPE_GPS_PREPARED
import com.jin.businfo_gumi.widget.EmptySignView.Companion.TYPE_GPS_FAILED
import com.jin.businfo_gumi.widget.MsgBottomSheetDialog
import com.jin.businfo_gumi.widget.NoticeDialog
import com.jin.businfo_gumi.widget.Toasty
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_search.*
import splitties.activities.start
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val firebaseViewModel by lazy { getViewModel<FirebaseViewModel>(viewModelFactory) }

    private val imm by lazy { getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }

    private val mainAdapter by lazy { MainAdapter() }
    private val searchHistoryAdapter by lazy { MainAdapter() }
    private val searchRecommendAdapter by lazy { MainAdapter() }
    private val searchQueryAdapter by lazy { MainAdapter() }

    private var hasLoadAdView1 = false
    private var hasLoadAdView2 = false

    private var onFavorite = true
    private var onGps = false
    private var onSearch = false
    private var onSearchQuery = false

    private var historyPage = 1
    private var recommendPage = 1

    private var launchBlock = true
    private var gpsBlock = false

    private val locationUtil by lazy { LocationUtil(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StatusBar.setStatusBarColor(this, R.color.WHITE)

        AdUtil.init(this)

        locationUtil
            .doOnUpdateLocation { lat, lng, dR ->
                if (onGps) {
                    mainAdapter.updateWithViewType(DataListUtil.gps(lat, lng, dR))
                    mainEmptySignView.mainToggle(mainAdapter, TYPE_GPS_FAILED)
                }
            }
            .doOnFailure {
                onGps = false
                if (it) {
                    mainAdapter.clear()
                    mainEmptySignView.mainToggle(mainAdapter, TYPE_GPS_FAILED)
                }
            }

        fetchData()
        setMainLayout()
        setSearchLayout()
        locationLayout.setOnClickListener { start<SettingActivity>() }
        searchInputView.setOnClickListener { switchLayout(true) }
    }

    private fun fetchData() {
        Debug.i("routeSize=${dataRouteList.size}")
        Debug.i("stationSize=${dataStationList.size}")
        Debug.i("stopbySize=${dataStopbyList.size}")

        firebaseViewModel.getNotice { msg, version ->
            NoticeDialog(this, msg) { dataPref.noticeVersion = version }.show()
            Debug.i("show notice (version:$version/myVersion:${dataPref.noticeVersion})")
        }

        firebaseViewModel.getDataVersion {
            Debug.i("[local:${dataPref.dataVersion}, server:$it] needToUpdate=${dataPref.dataVersion < it}")
            if (dataPref.dataVersion < it) firebaseViewModel.getFirebaseData {
                dataPref.dataVersion = it
                Debug.i("updateCompleted(version:$it)")
            }
        }
    }

    private fun setMainLayout() {
        bgAdView1.updateLayoutParams<ConstraintLayout.LayoutParams> {
            width = AdUtil.widthBannerPx
            height = AdUtil.heightBannerPx
        }
        if (!hasLoadAdView1) {
            hasLoadAdView1 = true
            AdUtil.load(adView1)
        }

        mainFab
            .doOnClickFavorite {
                if (!onFavorite) locationUtil.cancelTracking()
                onFavorite = true
                onGps = false
                mainAdapter.updateWithViewType(DataListUtil.favorite())
                mainEmptySignView.mainToggle(mainAdapter, TYPE_FAVORITE)
            }
            .doOnClickGPS {
                if (locationUtil.hasRequested) return@doOnClickGPS
                if (!onGps) {
                    gpsBlock = true
                    locationUtil.startTracking()
                }
                onFavorite = false
                onGps = true
                mainAdapter.clear()
                mainEmptySignView.mainToggle(mainAdapter, TYPE_GPS_PREPARED)
            }
            .doOnClickTheme {
                locationImageView.setImageDrawable(
                    VectorDrawableCompat.create(resources, R.drawable.ic_pin_drop_black_48dp, it)
                )
                searchHistoryIcon.setImageDrawable(
                    VectorDrawableCompat.create(
                        resources, R.drawable.ic_baseline_history_black_48dp, it
                    )
                )
                searchRecommendIcon.setImageDrawable(
                    VectorDrawableCompat.create(
                        resources, R.drawable.ic_baseline_thumb_up_black_48dp, it
                    )
                )
                mainAdapter.notifyDataSetChanged()
                mainEmptySignView.updateTheme()
            }
            .fetch()

        mainRecyclerView.adapter = mainAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainAdapter.updateWithViewType(DataListUtil.favorite())
        mainEmptySignView.mainToggle(mainAdapter, TYPE_FAVORITE)
    }

    private fun setSearchLayout() {
        bgAdView2.updateLayoutParams<FrameLayout.LayoutParams> {
            width = AdUtil.widthLargeBannerPx
            height = AdUtil.heightLargeBannerPx
        }

        searchBack.setOnClickListener { onBackPressed() }
        searchCloseBtn.setOnClickListener { searchEditText.setText("") }

        searchHistoryRecyclerView.adapter = searchHistoryAdapter
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        searchHistoryMore.setOnClickListener {
            historyPage++
            setHistory()
        }
        setHistory()

        searchRecommendRecyclerView.adapter = searchRecommendAdapter
        searchRecommendRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecommendMore.setOnClickListener {
            recommendPage++
            setRecommend()
        }
        setRecommend()

        searchHistoryDelete.setOnClickListener {
            MsgBottomSheetDialog.withTwoBtn(this)
                .setMessage(R.string.msgHistoryDelete)
                .setOnDoneListener {
                    dataPref.clearHistory()
                    setHistory()
                }
                .show()
        }

        searchQueryRecyclerView.adapter = searchQueryAdapter
        searchQueryRecyclerView.layoutManager = LinearLayoutManager(this)

        searchEditText.addTextChangedListener { editable ->
            val query = editable?.toString()
            if (query.isNullOrEmpty()) {
                onSearchQuery = false
                setHistory()
                setRecommend()
                searchCloseBtn.visibility = View.GONE
                searchQueryView.visibility = View.GONE
                searchContentView.visibility = View.VISIBLE
                searchNoResult.visibility = View.GONE
                return@addTextChangedListener
            } else {
                onSearchQuery = true
                searchCloseBtn.visibility = View.VISIBLE
                searchContentView.visibility = View.GONE
                searchQueryView.visibility = View.VISIBLE
            }
            val queryResult = DataListUtil.search(query)
            searchNoResult.visibility = if (queryResult.isEmpty()) View.VISIBLE else View.GONE
            searchQueryAdapter.updateWithViewType(queryResult)
        }
    }

    private fun setHistory(resetPage: Boolean = false): Boolean {
        val list = DataListUtil.history()
        val visible = list.isNotEmpty()
        if (!visible || resetPage) historyPage = 1
        val count = DataListUtil.getCountByPage(historyPage)
        searchHistoryTitle.visibility = if (visible) View.VISIBLE else View.GONE
        searchHistoryContent.visibility = if (visible) View.VISIBLE else View.GONE
        searchHistoryMore.visibility = if (list.size > count) View.VISIBLE else View.GONE
        searchHistoryAdapter.updateWithViewType(list.getListByPage(historyPage))
        return visible
    }

    private fun setRecommend(resetPage: Boolean = false): Boolean {
        val list = DataListUtil.recommend()
        val visible = list.isNotEmpty()
        if (!visible || resetPage) recommendPage = 1
        val count = DataListUtil.getCountByPage(recommendPage)
        searchRecommendTitle.visibility = if (visible) View.VISIBLE else View.GONE
        searchRecommendContent.visibility = if (visible) View.VISIBLE else View.GONE
        searchRecommendMore.visibility = if (list.size > count) View.VISIBLE else View.GONE
        searchRecommendAdapter.updateWithViewType(list.getListByPage(recommendPage))
        return visible
    }

    private fun switchLayout(turnOnSearchLayout: Boolean) {
        if (turnOnSearchLayout) {
            onSearch = true
            if (onGps) locationUtil.cancelTracking(doListener = false)

            locationLayout.visibility = View.GONE
            searchBack.visibility = View.VISIBLE
            searchInputView.visibility = View.GONE
            mainLayout.visibility = View.GONE
            searchLayout.visibility = View.VISIBLE

            if (!hasLoadAdView2) {
                hasLoadAdView2 = true
                AdUtil.load(adView2)
            }

            val historyVisible = setHistory(true)
            val recommendVisible = setRecommend(true)
            if (!historyVisible && !recommendVisible) {
                searchEditText.requestFocus()
                imm.showSoftInput(searchEditText, 0)
            }
        } else {
            searchInputView.visibility = View.VISIBLE
            searchBack.visibility = View.GONE
            locationLayout.visibility = View.VISIBLE
            searchLayout.visibility = View.GONE
            mainLayout.visibility = View.VISIBLE

            searchEditText.setText("")
            searchEditText.clearFocus()
            searchContentView.fullScroll(NestedScrollView.FOCUS_UP)
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)

            if (onFavorite) {
                mainAdapter.updateWithViewType(DataListUtil.favorite())
                mainEmptySignView.mainToggle(mainAdapter, TYPE_FAVORITE)
            } else if (onGps) {
                mainAdapter.notifyDataSetChanged()
                gpsBlock = true
                locationUtil.startTracking()
            }

            onSearch = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == RESPONSE_LOCATION_CANCEL) {
                locationUtil.cancelTracking()
                Toasty.alert(R.string.noLocationFunction)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() = when {
        onSearchQuery -> searchEditText.setText("")
        onSearch -> switchLayout(false)
        else -> CloseDialog(this).show()
    }

    override fun onResume() {
        super.onResume()
        Debug.i("[MainActivity]onResume")
        if (!launchBlock && !gpsBlock && reviewPref.needToShowReviewApi) {
            ReviewUtil.launchApi(this)
        }
        if (!onSearch) {
            if (onFavorite && !launchBlock) {
                mainAdapter.updateWithViewType(DataListUtil.favorite())
                mainEmptySignView.mainToggle(mainAdapter, TYPE_FAVORITE)
            } else if (onGps && !gpsBlock) {
                mainAdapter.notifyDataSetChanged()
                gpsBlock = true
                locationUtil.startTracking()
            } else {
                gpsBlock = false
            }
        } else {
            setHistory()
            setRecommend()
        }
        launchBlock = false
    }

    override fun onPause() {
        super.onPause()
        Debug.i("[MainActivity]onPause")
        if (!onSearch && onGps && !gpsBlock) locationUtil.cancelTracking(doListener = false)
    }
}