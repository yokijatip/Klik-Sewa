package com.gity.kliksewa.data.source.remote

import com.gity.kliksewa.data.model.BannerModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BannerRemoteSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getBanners(): List<BannerModel> = withContext(Dispatchers.IO) {
        try {
            // Ambil data dari Firestore
            val querySnapshot = firestore.collection("banner").get().await()

            // Konversi data ke List<BannerModel>
            querySnapshot.documents.mapNotNull { document ->
                document.toObject(BannerModel::class.java)
            }
        } catch (e: Exception) {
            // Jika terjadi error, kembalikan daftar kosong
            e.printStackTrace()
            emptyList()
        }
    }
}