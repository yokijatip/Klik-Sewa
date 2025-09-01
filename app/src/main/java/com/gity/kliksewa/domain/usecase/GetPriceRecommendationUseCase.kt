package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.domain.repository.MLRepository
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPriceRecommendationUseCase @Inject constructor(
    private val mlRepository: MLRepository
) {
    operator fun invoke(request: PriceRecommendationRequest): Flow<Resource<PriceRecommendationResponse>> {
        return mlRepository.getPriceRecommendation(request)
    }
}