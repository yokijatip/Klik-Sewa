package com.gity.kliksewa.data.model.ml

import com.google.gson.annotations.SerializedName

data class PriceAnalysis(
    @SerializedName("price_difference")
    val priceDifference: Double,

    @SerializedName("price_difference_percent")
    val priceDifferencePercent: Double,

    @SerializedName("status_detail")
    val statusDetail: String,

    @SerializedName("market_comparison")
    val marketComparison: String,

    @SerializedName("is_using_recommendation")
    val isUsingRecommendation: Boolean,

    @SerializedName("recommended_price_rp")
    val recommendedPriceRp: String,

    @SerializedName("current_price_rp")
    val currentPriceRp: String
)
