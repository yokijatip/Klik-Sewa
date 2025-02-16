package com.gity.kliksewa.ui.product.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.usecase.AddProductUseCase
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {
    private val _addProductState = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val addProductState = _addProductState.asStateFlow()

    fun addProduct(product: ProductModel) {
        viewModelScope.launch {
            addProductUseCase(product).collect { resource ->
                _addProductState.value = resource
            }
        }
    }
}