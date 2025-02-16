package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.ProductRepository
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: ProductModel): Flow<Resource<Unit>> {
        return productRepository.addProduct(product)
    }
}