package com.gity.kliksewa.domain.repository

import com.gity.kliksewa.data.model.BannerModel

interface BannerRepository {
    suspend fun getBanners(): List<BannerModel>
}
