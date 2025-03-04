package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.domain.repository.CartRepository
import com.gity.kliksewa.util.Resource
import javax.inject.Inject

class GetCartItemUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(userId: String): Resource<List<CartItemModel>> {
        return cartRepository.getCartItems(userId)
    }
}