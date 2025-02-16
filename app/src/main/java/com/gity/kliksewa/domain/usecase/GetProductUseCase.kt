package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(): List<ProductModel> {
        return productRepository.getRecommendedProducts()
    }
}