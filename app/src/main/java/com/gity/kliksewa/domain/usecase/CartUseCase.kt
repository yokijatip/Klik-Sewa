package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.domain.repository.CartRepository
import com.gity.kliksewa.util.Resource
import javax.inject.Inject

class CartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend fun deleteCartItem(userId: String, productId: String): Resource<Unit> {
        return cartRepository.removeFromCart(userId, productId)
    }

    suspend fun updateCartItemQuantity(userId: String, productId: String, newQuantity: Int): Resource<Unit> {
        return cartRepository.updateQuantity(userId, productId, newQuantity)
    }
}