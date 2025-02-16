package com.gity.kliksewa.ui.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.BannerModel
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.BannerRepository
import com.gity.kliksewa.domain.repository.ProductRepository
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bannerRepository: BannerRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    // State untuk banners
    private val _banners = MutableStateFlow<Resource<List<BannerModel>>>(Resource.Loading())
    val banners: StateFlow<Resource<List<BannerModel>>> = _banners.asStateFlow()

    // State untuk recommended products
    private val _recommendedProducts =
        MutableStateFlow<Resource<List<ProductModel>>>(Resource.Loading())
    val recommendedProducts: StateFlow<Resource<List<ProductModel>>> =
        _recommendedProducts.asStateFlow()

    init {
        loadBanners()
        loadRecommendedProducts()
    }

    // Load banners
    private fun loadBanners() {
        viewModelScope.launch {
            _banners.value = Resource.Loading()
            try {
                val bannerList = bannerRepository.getBanners()
                _banners.value = Resource.Success(bannerList)
            } catch (e: Exception) {
                _banners.value = Resource.Error(e.message ?: "Failed to load banners")
            }
        }
    }

    // Load recommended products
    private fun loadRecommendedProducts() {
        viewModelScope.launch {
            _recommendedProducts.value = Resource.Loading()
            try {
                val products = productRepository.getRecommendedProducts()
                _recommendedProducts.value = Resource.Success(products)
            } catch (e: Exception) {
                _recommendedProducts.value = Resource.Error(e.message ?: "Failed to load products")
            }
        }
    }
}