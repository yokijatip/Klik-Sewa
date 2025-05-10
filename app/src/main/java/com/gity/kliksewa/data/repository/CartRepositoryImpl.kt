package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.CartItemModel
import com.gity.kliksewa.domain.repository.CartRepository
import com.gity.kliksewa.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): CartRepository {
    override suspend fun addToCart(userId: String, cartItem: CartItemModel): Resource<Unit> {
        return try {
            val cartRef = firestore.collection("users").document(userId).collection("cart")
            cartRef.document(cartItem.productId).set(cartItem).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add to cart")
        }
    }

    override suspend fun getCartItems(userId: String): Resource<List<CartItemModel>> = withContext(Dispatchers.IO) {
        try {
            val cartItems = firestore
                .collection("users")
                .document(userId)
                .collection("cart")
                .get()
                .await()
                .toObjects(CartItemModel::class.java)
            Resource.Success(cartItems)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get cart items")
        }
    }

    override suspend fun removeFromCart(userId: String, productId: String): Resource<Unit> = withContext(Dispatchers.IO) {
         try {
            val cartRef = firestore.collection("users").document(userId).collection("cart")
            cartRef.document(productId).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove from cart")
        }
    }

    override suspend fun updateQuantity(
        userId: String,
        productId: String,
        newQuantity: Int
    ): Resource<Unit> = withContext(Dispatchers.IO) {
         try {
            val cartRef = firestore.collection("users").document(userId).collection("cart")
            cartRef.document(productId).update("quantity", newQuantity).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update quantity")
        }
    }
}