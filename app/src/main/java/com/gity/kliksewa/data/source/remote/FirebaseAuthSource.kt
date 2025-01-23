package com.gity.kliksewa.data.source.remote

import com.gity.kliksewa.data.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<UserModel> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val userDoc = firestore.collection("users").document(it.uid).get().await()
                val userData = userDoc.toObject(UserModel::class.java)
                Result.success(userData ?: UserModel(id = it.uid, email = it.email ?: ""))
            } ?: Result.failure(Exception("Login failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String): Result<UserModel> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            user?.let {
                val newUser = UserModel(
                    id = it.uid,
                    email = email,
                    fullName = "",
                    phoneNumber = ""
                )
                firestore.collection("users").document(it.uid).set(newUser).await()
                Result.success(newUser)
            } ?: Result.failure(Exception("Registration failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}