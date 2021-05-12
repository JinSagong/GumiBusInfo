package com.jin.businfo_gumi.di.module

import androidx.lifecycle.ViewModelProvider
import com.jin.businfo_gumi.di.factory.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
@Suppress("UNUSED")
abstract class ViewModelFactoryModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}