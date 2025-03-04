package com.gity.kliksewa.domain.repository

import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun addProduct(product: ProductModel): Flow<Resource<Unit>>
    suspend fun getRecommendedProducts(): List<ProductModel>
    suspend fun getProductById(productId: String): ProductModel
}