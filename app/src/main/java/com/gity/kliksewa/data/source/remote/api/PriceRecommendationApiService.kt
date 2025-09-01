package com.gity.kliksewa.data.source.remote.api

import com.gity.kliksewa.data.source.remote.response.AnalyzePriceResponse
import com.gity.kliksewa.data.source.remote.response.RecommendPriceResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface PriceRecommendationApiService {
    @POST("/recommend-price")
    suspend fun recommendPrice(@Body request: Map<String, Any>): RecommendPriceResponse

    @POST("/analyze-price")
    suspend fun analyzePrice(@Body request: Map<String, Any>): AnalyzePriceResponse
}