package com.jin.businfo_gumi.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("UNUSED")
abstract class BaseAdapter<T> : RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {
    val itemList = arrayListOf<Pair<T, Int>>()

    fun update(list: List<T>) = updateWithSingleViewType(list)

    fun updateWithViewType(list: List<Pair<T, Int>>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateWithSingleViewType(list: List<T>, viewType: Int = viewTypeDefault) {
        itemList.clear()
        itemList.addAll(list.map { Pair(it, viewType) })
        notifyDataSetChanged()
    }

    fun clear() {
        itemList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = itemList.size
    override fun getItemViewType(position: Int) = itemList[position].second
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.onBind(itemList[position].first, position)

    abstract inner class BaseViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        abstract fun onBind(item: T, position: Int)
    }

    companion object {
        const val viewTypeDefault = 0
    }
}