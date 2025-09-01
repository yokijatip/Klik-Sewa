package com.gity.kliksewa.data.model.ml

import com.google.gson.annotations.SerializedName

data class PriceAnalysisResponse(
    @SerializedName("current_price")
    val currentPrice: Double,

    @SerializedName("recommended_price")
    val recommendedPrice: Double,

    @SerializedName("price_analysis")
    val priceAnalysis: PriceAnalysis,

    @SerializedName("recommendation_status")
    val recommendationStatus: String,

    @SerializedName("confidence_score")
    val confidenceScore: Double,

    @SerializedName("timestamp")
    val timestamp: String
)
