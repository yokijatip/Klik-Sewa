package com.gity.kliksewa.domain.usecase

import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
import com.gity.kliksewa.domain.repository.MLRepository
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyzePricingUseCase @Inject constructor(
    private val mlRepository: MLRepository
) {
    operator fun invoke(request: PriceAnalysisRequest): Flow<Resource<PriceAnalysisResponse>> {
        return mlRepository.analyzePricing(request)
    }
}