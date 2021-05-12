package com.jin.businfo_gumi.ui.main

import android.annotation.SuppressLint
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.jin.businfo_gumi.MainApplication.Companion.dataPref
import com.jin.businfo_gumi.R
import com.jin.businfo_gumi.model.data.DataRoute
import com.jin.businfo_gumi.model.data.DataStation
import com.jin.businfo_gumi.ui.info.InfoActivity
import com.jin.businfo_gumi.util.LocationUtil
import com.jin.businfo_gumi.util.getTypePair
import com.jin.businfo_gumi.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_main.view.*
import splitties.activities.start

class MainAdapter : BaseAdapter<Any>() {
    companion object {
        const val viewTypeRoute = 1
        const val viewTypeStation = 2
        const val viewTypeGpsStation = 3
    }

    private val typedValue = TypedValue()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        viewTypeRoute -> MainRouteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        )
        viewTypeStation -> MainStationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        )
        viewTypeGpsStation -> MainGpsStationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        )
        else -> throw IllegalArgumentException()
    }

    inner class MainRouteViewHolder(v: View) : BaseViewHolder(v) {
        private val ivBg = v.itemMainBackground
        private val ivIcon = v.itemMainIconView
        private val tvTitle = v.itemMainTitleTextView
        private val tvType = v.itemMainTypeTextView
        private val llDescriptionRoute = v.itemMainDescriptionRoute
        private val tvDescriptionRoute1 = v.itemMainDescriptionRoute1
        private val tvDescriptionRoute2 = v.itemMainDescriptionRoute2
        private val ivFavorite = v.mainMainFavoriteIconView

        override fun onBind(item: Any, position: Int) = onBind(item as DataRoute)

        @SuppressLint("SetTextI18n")
        private fun onBind(item: DataRoute) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                itemView.context.theme.resolveAttribute(R.attr.themeColor, typedValue, true)
                val color = ContextCompat.getColor(itemView.context, typedValue.resourceId)
                ivBg.outlineAmbientShadowColor = color
                ivBg.outlineSpotShadowColor = color
            } else {
                ivBg.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.bg_main_item)
            }
            ivIcon.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    R.drawable.ic_bus_48dp,
                    itemView.context.theme
                )
            )

            tvTitle.text = item.routeNo

            val typePair = getTypePair(itemView.context, item.routeType)
            tvType.text = typePair.first
            tvType.backgroundTintList = typePair.second
            tvType.visibility = View.VISIBLE

            llDescriptionRoute.visibility = View.VISIBLE
            tvDescriptionRoute1.text = "기점: ${item.startStation}"
            tvDescriptionRoute2.text = "종점: ${item.endStation}"

            var isFavorite = dataPref.getFavorite().contains(item.routeId)
            ivFavorite.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                    itemView.context.theme
                )
            )
            ivFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) dataPref.addFavorite(item.routeId)
                else dataPref.removeFavorite(item.routeId)
                ivFavorite.setImageDrawable(
                    VectorDrawableCompat.create(
                        itemView.context.resources,
                        if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                        itemView.context.theme
                    )
                )
            }

            itemView.setOnClickListener {
                itemView.context.start<InfoActivity> { putExtra("dataRoute", item) }
            }
        }
    }

    inner class MainStationViewHolder(v: View) : BaseViewHolder(v) {
        private val ivBg = v.itemMainBackground
        private val ivIcon = v.itemMainIconView
        private val tvTitle = v.itemMainTitleTextView
        private val tvDescriptionStation = v.itemMainDescriptionStation
        private val ivFavorite = v.mainMainFavoriteIconView

        override fun onBind(item: Any, position: Int) = onBind(item as DataStation)

        private fun onBind(item: DataStation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                itemView.context.theme.resolveAttribute(R.attr.themeColor, typedValue, true)
                val color = ContextCompat.getColor(itemView.context, typedValue.resourceId)
                ivBg.outlineAmbientShadowColor = color
                ivBg.outlineSpotShadowColor = color
            } else {
                ivBg.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.bg_main_item)
            }
            ivIcon.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    R.drawable.ic_bus_stop_48dp,
                    itemView.context.theme
                )
            )

            tvTitle.text = item.stationName

            tvDescriptionStation.visibility = View.VISIBLE
            tvDescriptionStation.text = item.stationNo

            var isFavorite = dataPref.getFavorite().contains(item.stationId)
            ivFavorite.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                    itemView.context.theme
                )
            )
            ivFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) dataPref.addFavorite(item.stationId)
                else dataPref.removeFavorite(item.stationId)
                ivFavorite.setImageDrawable(
                    VectorDrawableCompat.create(
                        itemView.context.resources,
                        if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                        itemView.context.theme
                    )
                )
            }

            itemView.setOnClickListener {
                itemView.context.start<InfoActivity> { putExtra("dataStation", item) }
            }
        }
    }

    inner class MainGpsStationViewHolder(v: View) : BaseViewHolder(v) {
        private val ivBg = v.itemMainBackground
        private val ivIcon = v.itemMainIconView
        private val tvTitle = v.itemMainTitleTextView
        private val tvDescriptionStation = v.itemMainDescriptionStation
        private val clDistance = v.itemMainDistanceLayout
        private val ivDistance = v.itemMainDistanceImageView
        private val tvDistance = v.itemMainDistanceTextView
        private val ivFavorite = v.mainMainFavoriteIconView

        @Suppress("UNCHECKED_CAST")
        override fun onBind(item: Any, position: Int) = onBind(item as Pair<DataStation, Double>)
        private fun onBind(item: Pair<DataStation, Double>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                itemView.context.theme.resolveAttribute(R.attr.themeColor, typedValue, true)
                val color = ContextCompat.getColor(itemView.context, typedValue.resourceId)
                ivBg.outlineAmbientShadowColor = color
                ivBg.outlineSpotShadowColor = color
            } else {
                ivBg.background =
                    ContextCompat.getDrawable(itemView.context, R.drawable.bg_main_item)
            }
            ivIcon.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    R.drawable.ic_bus_stop_48dp,
                    itemView.context.theme
                )
            )

            tvTitle.text = item.first.stationName

            tvDescriptionStation.visibility = View.VISIBLE
            tvDescriptionStation.text = item.first.stationNo

            clDistance.visibility = View.VISIBLE
            ivDistance.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    R.drawable.ic_my_location_black_48dp,
                    itemView.context.theme
                )
            )
            val themeColorAttribute =
                itemView.context.obtainStyledAttributes(intArrayOf(R.attr.themeColor))
            tvDistance.setTextColor(themeColorAttribute.getColorStateList(0))
            themeColorAttribute.recycle()
            tvDistance.text = LocationUtil.getDistanceText(item.second)

            var isFavorite = dataPref.getFavorite().contains(item.first.stationId)
            ivFavorite.setImageDrawable(
                VectorDrawableCompat.create(
                    itemView.context.resources,
                    if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                    itemView.context.theme
                )
            )
            ivFavorite.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) dataPref.addFavorite(item.first.stationId)
                else dataPref.removeFavorite(item.first.stationId)
                ivFavorite.setImageDrawable(
                    VectorDrawableCompat.create(
                        itemView.context.resources,
                        if (isFavorite) R.drawable.ic_favorite_black_48dp else R.drawable.ic_favorite_border_black_48dp,
                        itemView.context.theme
                    )
                )
            }

            itemView.setOnClickListener {
                itemView.context.start<InfoActivity> { putExtra("dataStation", item.first) }
            }
        }
    }
}