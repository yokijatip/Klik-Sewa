package com.gity.kliksewa.data.model

data class UserModel(
    val id: String = "",
    val email: String = "",
    val role: String = "",
    val fullName: String? = "",
    val phoneNumber: String? = "",
    val profileImageUrl: String? = ""
)
