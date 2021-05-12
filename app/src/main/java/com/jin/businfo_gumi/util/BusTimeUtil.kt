package com.jin.businfo_gumi.util

@Suppress("UNUSED")
class BusTimeUtil(private val time: Int?, private val count: Int?) {
    fun getBusTime() = if (time == null) ""
    else if (time / 60 <= 1 || count == 1) "곧"
    else "${time / 60}"

    fun getBusTimeUnit() = if (time == null) "도착 정보 없음"
    else if (time / 60 <= 1 || count == 1) "도착"
    else "분 후"
}