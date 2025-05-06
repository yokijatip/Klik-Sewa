package com.gity.kliksewa.ui.main.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.ProductRepository
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    // State untuk list random product
    private val _randomProducts = MutableStateFlow<Resource<List<ProductModel>>>(Resource.Loading())
    val randomProducts: StateFlow<Resource<List<ProductModel>>> = _randomProducts.asStateFlow()

    init {
        loadRandomProducts()
    }

    // Fungsi ini bisa dipanggil dari fragment untuk muat ulang data
    fun loadRandomProducts() {
        viewModelScope.launch {
            _randomProducts.value = Resource.Loading()
            try {
                val randomProducts = productRepository.getRandomProducts()
                _randomProducts.value = Resource.Success(randomProducts)
                Timber.d("Loaded ${randomProducts.size} products")
            } catch (e: Exception) {
                _randomProducts.value =
                    Resource.Error(e.message ?: "Failed to load random products")
                Timber.e("Error loading products: ${e.message}")
            }
        }
    }
}