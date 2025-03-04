package com.gity.kliksewa.domain.repository

import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.util.Resource

interface CartRepository {
    suspend fun addToCart(userId: String, cartItem: CartItemModel): Resource<Unit>
    suspend fun getCartItems(userId: String): Resource<List<CartItemModel>>
}