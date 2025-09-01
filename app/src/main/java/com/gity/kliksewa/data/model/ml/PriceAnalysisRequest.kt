package com.gity.kliksewa.data.model.ml

data class PriceAnalysisRequest(
    val category: String,
    val subcategory: String,
    val name: String,
    val city: String,
    val district: String,
    val condition: String,
    val type: String,
    val current_price: Double
)
