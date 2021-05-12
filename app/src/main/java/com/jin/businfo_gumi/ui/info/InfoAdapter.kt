package com.jin.businfo_gumi.ui.info

import android.annotation.SuppressLint
import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.*
import com.jin.businfo_gumi.util.BusTimeUtil
import com.jin.businfo_gumi.util.getTypePair
import com.jin.businfo_gumi.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_info_for_route.view.*
import kotlinx.android.synthetic.main.item_info_for_station.view.*
import splitties.activities.start

class InfoAdapter(private val activity: Activity) : BaseAdapter<Any>() {
    companion object {
        const val viewTypeForRoute = 1
        const val viewTypeForStation = 2
    }

    private val typedValue = TypedValue()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        viewTypeForRoute -> InfoForRouteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_info_for_route, parent, false)
        )
        viewTypeForStation -> InfoForStationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_info_for_station, parent, false)
        )
        else -> throw IllegalArgumentException()
    }

    inner class InfoForRouteViewHolder(v: View) : BaseViewHolder(v) {
        private val tvTitle = v.itemInfoForRouteTitle
        private val tvDescriptionStation = v.itemInfoForRouteNumber
        private val ivLineTop = v.itemInfoForRouteLine1
        private val ivLineBottom = v.itemInfoForRouteLine2
        private val llContent = v.itemInfoForRouteContent
        private val ivBus1 = v.itemInfoForRouteBus1
        private val ivBus2 = v.itemInfoForRouteBus2

        @Suppress("UNCHECKED_CAST")
        override fun onBind(item: Any, position: Int) =
            onBind(item as Pair<DataStation, List<DataLiveItemForRoute>?>, position)

        private fun onBind(item: Pair<DataStation, List<DataLiveItemForRoute>?>, position: Int) {
            tvTitle.text = item.first.stationName
            tvDescriptionStation.text = item.first.stationNo
            ivLineTop.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
            ivLineBottom.visibility =
                if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE

            llContent.setOnClickListener {
                itemView.context.start<InfoActivity> { putExtra("dataStation", item.first) }
                activity.finish()
            }

            ivBus1.visibility = if (item.second?.size == 1) View.VISIBLE else View.INVISIBLE
            ivBus2.visibility = if (item.second?.size ?: 0 >= 2) View.VISIBLE else View.INVISIBLE
        }
    }

    inner class InfoForStationViewHolder(v: View) : BaseViewHolder(v) {
        private val tvTitle = v.itemInfoForStationTitle
        private val tvType = v.itemInfoForStationType
        private val tvDescription = v.itemInfoForStationDescription
        private val tvTime = v.itemInfoForStationTime
        private val tvTimeUnit = v.itemInfoForStationTimeUnit
        private val clPrevLayout = v.itemInfoForStationPrevLayout
        private val tvPrevStation = v.itemInfoForStationPrevStation
        private val tvPrevCount = v.itemInfoForStationPrevCount

        @Suppress("UNCHECKED_CAST")
        override fun onBind(item: Any, position: Int) =
            onBind(item as Triple<DataRoute, DataLiveItemForStation?, String?>)

        @SuppressLint("SetTextI18n")
        private fun onBind(item: Triple<DataRoute, DataLiveItemForStation?, String?>) {
            tvTitle.text = item.first.routeNo

            val typePair = getTypePair(itemView.context, item.first.routeType)
            tvType.text = typePair.first
            tvType.backgroundTintList = typePair.second
            tvDescription.text = "${item.first.endStation} 방면"

            val busTimeUtil = BusTimeUtil(item.second?.arrTime, item.second?.prevStationCount)
            tvTime.text = busTimeUtil.getBusTime()
            tvTimeUnit.text = busTimeUtil.getBusTimeUnit()
            if (item.second?.arrTime != null) {
                itemView.context.theme.resolveAttribute(R.attr.themeColor, typedValue, true)
                tvTimeUnit.setTextColor(
                    ContextCompat.getColor(itemView.context, typedValue.resourceId)
                )
            } else {
                tvTimeUnit.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_gray))
            }

            if (item.second != null && item.third != null) {
                clPrevLayout.visibility = View.VISIBLE
                tvPrevCount.text = "${item.second!!.prevStationCount}정거장 전"
                tvPrevStation.text = item.third
            } else clPrevLayout.visibility = View.GONE

            itemView.setOnClickListener {
                itemView.context.start<InfoActivity> { putExtra("dataRoute", item.first) }
                activity.finish()
            }
        }
    }
}