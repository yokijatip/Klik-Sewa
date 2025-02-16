package com.gity.kliksewa.data.model

data class ProductModel(
    val id: String = "",
    val ownerId: String = "",
    val totalRents: Int = 0,
    val rating: Double = 0.0,
    val reviews: List<ReviewModel> = emptyList(),
    val status: String = "",
    val name: String = "",
    val description: String = "",
    val address: String = "",
    val category: String = "",
    val unitPrice: Double = 0.0,
    val pricePerHour: Double? = null,
    val pricePerDay: Double? = null,
    val pricePerWeek: Double? = null,
    val pricePerMonth: Double? = null,
    val images: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)