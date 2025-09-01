package com.gity.kliksewa.data.source.remote.response

data class AnalyzePriceResponse(
	val confidenceScore: Any? = null,
	val recommendationStatus: String? = null,
	val currentPrice: Int? = null,
	val recommendedPrice: Int? = null,
	val priceAnalysis: PriceAnalysis? = null,
	val timestamp: String? = null
)

data class PriceAnalysis(
	val priceDifference: Int? = null,
	val priceDifferencePercent: Int? = null,
	val marketComparison: String? = null,
	val recommendedPriceRp: String? = null,
	val isUsingRecommendation: Boolean? = null,
	val currentPriceRp: String? = null,
	val statusDetail: String? = null
)

