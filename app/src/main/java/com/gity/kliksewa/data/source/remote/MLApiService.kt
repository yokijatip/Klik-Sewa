package com.gity.kliksewa.data.source.remote

import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MLApiService {
    @POST("recommend-price")
    suspend fun getPriceRecommendation(
        @Body request: PriceRecommendationRequest
    ): Response<PriceRecommendationResponse>

    @POST("analyze-price")
    suspend fun analyzePricing(
        @Body request: PriceAnalysisRequest
    ): Response<PriceAnalysisResponse>
}