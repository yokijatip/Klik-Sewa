package com.gity.kliksewa.data.model

data class ReviewModel (
    val reviewId: String = "",
    val rentalId: String = "",
    val itemId: String = "",
    val rating: Double = 0.0,
    val comment: String = "",
    val image: String = "",
    val createdAt: Long = System.currentTimeMillis()
)