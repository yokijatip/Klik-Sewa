package com.gity.kliksewa.ui.main.user.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.AddressModel
import com.gity.kliksewa.domain.repository.AddressRepository
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository
) : ViewModel() {

    private val _addresses = MutableStateFlow<Resource<List<AddressModel>>?>(null)
    val addresses: StateFlow<Resource<List<AddressModel>>?> get() = _addresses

    fun getAddresses(userId: String) {
        viewModelScope.launch {
            _addresses.value = Resource.Loading()
            _addresses.value = addressRepository.getAddress(userId)
        }
    }

}