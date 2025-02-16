package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): ProductModel {
        return productRepository.getProductById(productId)
    }
}