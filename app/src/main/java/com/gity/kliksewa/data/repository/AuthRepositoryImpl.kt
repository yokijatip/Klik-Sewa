package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.UserModel
import com.gity.kliksewa.data.source.remote.FirebaseAuthSource
import com.gity.kliksewa.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuthSource: FirebaseAuthSource) {
    suspend fun login(email: String, password: String): Resource<UserModel> {
        return try {
            val result = firebaseAuthSource.login(email, password)
            if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    suspend fun register(role: String, fullName: String, phoneNumber: String, email: String, password: String): Resource<UserModel> {
        return try {
            val result = firebaseAuthSource.register(role, fullName, phoneNumber, email, password)
            if (result.isSuccess) {
                Resource.Success(result.getOrNull()!!)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    suspend fun logout(): Resource<Boolean> {
        return try {
            // Panggil fungsi logout dari FirebaseAuthSource
            val result = firebaseAuthSource.logout()
            if (result.isSuccess) {
                Resource.Success(true)
            } else {
                Resource.Error(result.exceptionOrNull()?.message ?: "Logout failed")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }
}