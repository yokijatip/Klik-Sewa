package com.gity.kliksewa.ui.main.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.domain.usecase.CartUseCase
import com.gity.kliksewa.domain.usecase.GetCartItemUseCase
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartItemUseCase: GetCartItemUseCase,
    private val cartUseCase: CartUseCase
) : ViewModel() {

    private val _cartItems = MutableStateFlow<Resource<List<CartItemModel>>?>(null)
    val cartItems: StateFlow<Resource<List<CartItemModel>>?> get() = _cartItems

    fun getCartItems(userId: String) {
        viewModelScope.launch {
            _cartItems.value = Resource.Loading()
            _cartItems.value = getCartItemUseCase(userId)
        }
    }

    fun updateCartItemQuantity(userId: String, productId: String, newQuantity: String) {
        viewModelScope.launch {

        }
    }
}