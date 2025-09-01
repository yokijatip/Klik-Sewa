package com.gity.kliksewa.data.model.ml

import com.google.gson.annotations.SerializedName

data class PriceRecommendationResponse(
    @SerializedName("recommended_price_daily")
    val recommendedPriceDaily: Double,

    @SerializedName("market_analysis")
    val marketAnalysis: MarketAnalysis,

    @SerializedName("model_information")
    val modelInformation: ModelInformation,

    @SerializedName("analysis_factors")
    val analysisFactors: List<String>,

    @SerializedName("confidence_score")
    val confidenceScore: Double,

    @SerializedName("timestamp")
    val timestamp: String
)
