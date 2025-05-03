package com.gity.kliksewa.ui.main.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gity.kliksewa.data.model.UserModel
import com.gity.kliksewa.data.repository.AuthRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authRepositoryImpl: AuthRepositoryImpl
) : ViewModel() {

    // LiveData for user data
    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData

    init {
        loadUserData()
    }

    // Load user data from SharedPreferences
    private fun loadUserData() {
        val user = authRepositoryImpl.getUserData()
        _userData.value = user
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return authRepositoryImpl.isLoggedIn()
    }

    fun logout() {
        viewModelScope.launch {
            authRepositoryImpl.logout()
            _userData.postValue(null)
        }
    }
}