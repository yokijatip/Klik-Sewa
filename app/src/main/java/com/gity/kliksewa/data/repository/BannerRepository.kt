package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.BannerModel

interface BannerRepository {
    suspend fun getBanners(): Result<List<BannerModel>>
}