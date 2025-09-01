package com.gity.kliksewa.ui.product.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.domain.usecase.AddProductUseCase
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
class AddProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getPriceRecommendationUseCase: GetPriceRecommendationUseCase
) : ViewModel() {
    private val _addProductState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val addProductState = _addProductState.asStateFlow()

    private val _priceRecommendationState =
        MutableStateFlow<Resource<PriceRecommendationResponse>?>(null)
    val priceRecommendationState: StateFlow<Resource<PriceRecommendationResponse>?> =
        _priceRecommendationState.asStateFlow()

    fun addProduct(product: ProductModel) {
        viewModelScope.launch {
            addProductUseCase(product).collect { resource ->
                _addProductState.value = resource
            }
        }
    }

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

            Timber.tag("AddProductViewModel").d("Getting price recommendation for: $name")

            getPriceRecommendationUseCase(request).collect { result ->
                _priceRecommendationState.value = result
                when (result) {
                    is Resource.Success -> {
                        Timber.tag("AddProductViewModel")
                            .d("Price recommendation success: ${result.data?.recommendedPriceDaily}")
                    }

                    is Resource.Error -> {
                        Timber.tag("AddProductViewModel")
                            .e("Price recommendation error: ${result.message}")
                    }

                    is Resource.Loading -> {
                        Timber.tag("AddProductViewModel").d("Getting price recommendation...")
                    }
                }
            }
        }
    }

    fun clearPriceRecommendationState() {
        _priceRecommendationState.value = null
    }

    fun validateRecommendationFields(
        category: String,
        subcategory: String,
        name: String,
        city: String,
        district: String,
        condition: String,
        type: String
    ): Boolean {
        return category.isNotEmpty() &&
                subcategory.isNotEmpty() &&
                name.isNotEmpty() &&
                city.isNotEmpty() &&
                district.isNotEmpty() &&
                condition.isNotEmpty() &&
                type.isNotEmpty()
    }
}