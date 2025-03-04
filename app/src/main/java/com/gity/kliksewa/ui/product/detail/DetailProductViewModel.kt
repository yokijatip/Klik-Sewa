package com.gity.kliksewa.ui.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.usecase.AddToCartUseCase
import com.gity.kliksewa.domain.usecase.GetProductByIdUseCase
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailProductViewModel @Inject constructor(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val addToCartUseCase: AddToCartUseCase
) : ViewModel() {
    private val _productDetails = MutableLiveData<Resource<ProductModel>>(Resource.Loading())
    val productDetails: LiveData<Resource<ProductModel>> get() = _productDetails

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            _productDetails.value = Resource.Loading()
            try {
                val product = getProductByIdUseCase(productId)
                _productDetails.value = Resource.Success(product)
                Timber.tag("DetailProductViewModel").e("Fetching product details for ID: $productId")
            } catch (e:Exception) {
                _productDetails.value = Resource.Error(e.message ?: "Failed to load product details")
                Timber.tag("DetailProductViewModel").e("Failed to load product details: ${e.message}")
            }
        }
    }

    private val _addToCartResult = MutableStateFlow<Resource<Unit>?>(null)
    val addToCartResult: StateFlow<Resource<Unit>?> get() = _addToCartResult

    fun addToCart(userId: String, cartItem: CartItemModel) {
        viewModelScope.launch {
            _addToCartResult.value = Resource.Loading()
            _addToCartResult.value = addToCartUseCase(userId, cartItem)
        }
    }

}