package com.jin.businfo_gumi.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNUSED")
inline fun <reified VM : ViewModel> FragmentActivity.getViewModel(provider: ViewModelProvider.Factory) =
    ViewModelProvider(this, provider).get(VM::class.java)

@Suppress("UNUSED")
inline fun <reified VM : ViewModel> Fragment.getViewModel(provider: ViewModelProvider.Factory) =
    ViewModelProvider(requireActivity(), provider).get(VM::class.java)

@Suppress("UNUSED")
inline fun <reified VM : ViewModel> DialogFragment.getViewModel(provider: ViewModelProvider.Factory) =
    ViewModelProvider(requireActivity(), provider).get(VM::class.java)