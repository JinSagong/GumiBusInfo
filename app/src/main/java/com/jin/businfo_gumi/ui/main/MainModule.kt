package com.jin.businfo_gumi.ui.main

import androidx.lifecycle.ViewModel
import com.jin.businfo_gumi.di.annotation.ViewModelKey
import com.jin.businfo_gumi.viewmodel.FirebaseViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("UNUSED")
internal abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(FirebaseViewModel::class)
    internal abstract fun bindFirebaseViewModel(viewModel: FirebaseViewModel): ViewModel
}