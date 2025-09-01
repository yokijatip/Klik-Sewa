package com.gity.kliksewa.data.model.ml

import com.google.gson.annotations.SerializedName

data class MarketAnalysis(
    @SerializedName("price_range_rp")
    val priceRangeRp: String,

    @SerializedName("market_average_rp")
    val marketAverageRp: String,

    @SerializedName("competitive_position")
    val competitivePosition: String,

    @SerializedName("price_range")
    val priceRange: List<Double>,

    @SerializedName("market_average")
    val marketAverage: Double
)
