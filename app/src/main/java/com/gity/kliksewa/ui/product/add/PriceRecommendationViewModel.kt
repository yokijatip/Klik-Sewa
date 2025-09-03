package com.gity.kliksewa.ui.product.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.domain.usecase.GetPriceRecommendationUseCase
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PriceRecommendationViewModel @Inject constructor(
    private val getPriceRecommendationUseCase: GetPriceRecommendationUseCase
): ViewModel() {
    private val _priceRecommendationState = MutableStateFlow<Resource<PriceRecommendationResponse>>(Resource.Loading())
    val priceRecommendationState: StateFlow<Resource<PriceRecommendationResponse>> = _priceRecommendationState.asStateFlow()

    fun getPriceRecommendation(
        category: String,
        subcategory: String,
        name: String,
        city: String,
        district: String,
        condition: String,
        type: String
    ) {
        viewModelScope.launch {
            val request = PriceRecommendationRequest(
                category = category,
                subcategory = subcategory,
                name = name,
                city = city,
                district = district,
                condition = condition,
                type = type
            )

            Timber.tag("PriceRecommendationViewModel").d("Getting price recommendation for: $name")

            getPriceRecommendationUseCase(request).collect { result ->
                _priceRecommendationState.value = result
                when (result) {
                    is Resource.Error -> {
                        Timber.tag("PriceRecommendationViewModel").d("Error getting price recommendation: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Timber.tag("PriceRecommendationViewModel").d("Loading price recommendation........")
                    }
                    is Resource.Success -> {
                        Timber.tag("PriceRecommendationViewModel").d("Price recommendation retrieved successfully: ${result.data?.recommendedPriceDaily}")
                    }
                }
            }
        }
    }
}