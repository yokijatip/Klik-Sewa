package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.BannerModel
import com.gity.kliksewa.data.source.remote.BannerRemoteSource
import com.gity.kliksewa.domain.repository.BannerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRepositoryImpl @Inject constructor(
    private val remoteSource: BannerRemoteSource
) : BannerRepository {
    override suspend fun getBanners(): List<BannerModel> {
        return remoteSource.getBanners()
    }
}