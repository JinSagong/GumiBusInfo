package com.jin.businfo_gumi.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.jin.businfo_gumi.R

@Suppress("UNUSED")
fun getTypePair(context: Context, type: String?) =
    if (type == "좌석버스") Pair("좌석", ContextCompat.getColorStateList(context, R.color.type2))
    else Pair("일반", ContextCompat.getColorStateList(context, R.color.type1))