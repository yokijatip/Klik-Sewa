package com.gity.kliksewa.data.source.remote

import com.gity.kliksewa.data.model.BannerModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BannerRemoteSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getBanners(): Result<List<BannerModel>> = withContext(Dispatchers.IO) {
        try {
            val banners = mutableListOf<BannerModel>()
            val querySnapshot = firestore.collection("banner").get().await()

            for (document in querySnapshot.documents) {
                document.toObject(BannerModel::class.java)?.let { banner ->
                    banners.add(banner)
                }
            }
            Result.success(banners)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}