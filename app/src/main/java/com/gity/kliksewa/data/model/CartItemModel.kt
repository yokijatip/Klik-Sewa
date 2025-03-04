package com.gity.kliksewa.data.model

data class CartItemModel(
    val productId: String = "",
    val productName: String = "",
    val productAddress: String = "",
    val productPriceType: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val imageUrl: String = ""
)
