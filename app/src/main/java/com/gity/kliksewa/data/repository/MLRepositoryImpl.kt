package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.data.source.remote.MLRemoteDataSource
import com.gity.kliksewa.domain.repository.MLRepository
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class MLRepositoryImpl @Inject constructor(
    private val remoteDataSource: MLRemoteDataSource
): MLRepository {
    override fun getPriceRecommendation(
        request: PriceRecommendationRequest
    ): Flow<Resource<PriceRecommendationResponse>> {
        Timber.tag("MLRepositoryImpl").d("Getting price recommendation for category: ${request.category}")
        return remoteDataSource.getPriceRecommendation(request)
    }

    override fun analyzePricing(
        request: PriceAnalysisRequest
    ): Flow<Resource<PriceAnalysisResponse>> {
        Timber.tag("MLRepositoryImpl").d("Analyzing price: ${request.current_price} for ${request.name}")
        return remoteDataSource.analyzePricing(request)
    }
}