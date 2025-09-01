package com.gity.kliksewa.domain.repository

import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow

interface MLRepository {
    fun getPriceRecommendation(request: PriceRecommendationRequest): Flow<Resource<PriceRecommendationResponse>>
    fun analyzePricing(request: PriceAnalysisRequest): Flow<Resource<PriceAnalysisResponse>>
}