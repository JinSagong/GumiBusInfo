package com.jin.businfo_gumi.ui.info

import androidx.lifecycle.ViewModel
import com.jin.businfo_gumi.di.annotation.ViewModelKey
import com.jin.businfo_gumi.viewmodel.BusViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
internal abstract class InfoModule {
    @Binds
    @IntoMap
    @ViewModelKey(BusViewModel::class)
    internal abstract fun bindBusViewModel(viewModel: BusViewModel): ViewModel
}