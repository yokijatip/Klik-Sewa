package com.gity.kliksewa.data.source.remote.response

data class RecommendPriceResponse(
	val modelInformation: ModelInformation? = null,
	val confidenceScore: Any? = null,
	val analysisFactors: List<String?>? = null,
	val marketAnalysis: MarketAnalysis? = null,
	val recommendedPriceDaily: Int? = null,
	val timestamp: String? = null
)

data class ModelInformation(
	val modelAccuracy: Any? = null,
	val mae: Any? = null,
	val confidenceScore: Int? = null,
	val rmse: Any? = null,
	val trainingDataPoints: Int? = null,
	val algorithm: String? = null
)

data class MarketAnalysis(
	val competitivePosition: String? = null,
	val marketAverage: Int? = null,
	val priceRangeRp: String? = null,
	val priceRange: List<Int?>? = null,
	val marketAverageRp: String? = null
)

