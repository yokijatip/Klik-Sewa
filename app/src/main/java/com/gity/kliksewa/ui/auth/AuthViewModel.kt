package com.gity.kliksewa.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.UserModel
import com.gity.kliksewa.data.repository.AuthRepository
import com.gity.kliksewa.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _loginResult = MutableLiveData<Resource<UserModel>>()
    val loginResult: LiveData<Resource<UserModel>> = _loginResult

    private val _registerResult = MutableLiveData<Resource<UserModel>>()
    val registerResult: LiveData<Resource<UserModel>> = _registerResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            _loginResult.value = authRepository.login(email, password)
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            _registerResult.value = authRepository.register(email, password)
        }
    }
}