package com.gity.kliksewa.di

import com.gity.kliksewa.data.repository.BannerRepository
import com.gity.kliksewa.data.repository.BannerRepositoryImpl
import com.gity.kliksewa.data.source.remote.BannerRemoteSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBannerRepository(
        remoteSource: BannerRemoteSource
    ): BannerRepository {
        return BannerRepositoryImpl(remoteSource)
    }
}