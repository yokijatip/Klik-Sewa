package com.gity.kliksewa.util

import android.content.Context
import com.gity.kliksewa.data.model.UserModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(@ApplicationContext context: Context){
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val gson = Gson()

    // Constants
    companion object {
        private const val KEY_USER_DATA = "user_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Save user data
    fun saveUserData(user: UserModel) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit()
            .putString(KEY_USER_DATA, userJson)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    // Get user data
    fun getUserData(): UserModel? {
        val userJson = sharedPreferences.getString(KEY_USER_DATA, null)
        return if (userJson != null) {
            gson.fromJson(userJson, UserModel::class.java)
        } else {
            null
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Clear user data (for logout)
    fun clearUserData() {
        sharedPreferences.edit()
            .remove(KEY_USER_DATA)
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .apply()
    }
}