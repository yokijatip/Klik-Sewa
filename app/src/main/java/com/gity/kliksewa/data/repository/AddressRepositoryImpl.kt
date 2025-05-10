package com.gity.kliksewa.data.repository

import com.gity.kliksewa.data.model.AddressModel
import com.gity.kliksewa.domain.repository.AddressRepository
import com.gity.kliksewa.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AddressRepository {
    override suspend fun getAddress(userId: String): Resource<List<AddressModel>> =
        withContext(Dispatchers.IO) {
            try {
                val addresses = firestore
                    .collection("users")
                    .document(userId)
                    .collection("addresses")
                    .get()
                    .await()
                    .toObjects(AddressModel::class.java)
                Timber.tag("AddressRepositoryImpl").d("Addresses fetched successfully")
                Resource.Success(addresses)
            } catch (e: Exception) {
                Timber.tag("AddressRepositoryImpl").e("Error fetching addresses: ${e.message}")
                Resource.Error(e.message ?: "Unknown error occurred")
            }
        }

    override suspend fun addAddress(address: AddressModel): Resource<Boolean> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: return Resource.Error("User not authenticated")
            val addressId = firestore
                .collection("users")
                .document(userId)
                .collection("addresses")
                .document()
                .id

            firestore
                .collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .set(address.copy(id = addressId))
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun updateAddress(address: AddressModel): Resource<Boolean> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            firestore
                .collection("users")
                .document(userId)
                .collection("addresses")
                .document(address.id)
                .set(address)
                .await()


            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            firestore
                .collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
                .delete()
                .await()

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
}