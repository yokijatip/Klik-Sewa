package com.gity.kliksewa.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.gity.kliksewa.data.model.ProductModel
import com.gity.kliksewa.domain.repository.ProductRepository
import com.gity.kliksewa.helper.CommonUtils.uriToFile
import com.gity.kliksewa.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val cloudinary: Cloudinary,
    private val context: Context
) : ProductRepository {
    override suspend fun addProduct(product: ProductModel) = flow {
        emit(Resource.Loading())
        try {
            // Upload gambar ke Cloudinary
            val uploadedImageUrls = uploadImagesToCloudinary(product.images, product.category)

            // Buat objek produk baru dengan URL gambar yang sudah diunggah
            val updatedProduct = product.copy(images = uploadedImageUrls)

            // Simpan produk ke Firestore
            firestore.collection("products").document(updatedProduct.id).set(updatedProduct).await()

            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to add product"))
        }
    }

    override suspend fun getRecommendedProducts(): List<ProductModel> {
        return try {
            // Ambil data produk dari Firestore
            val querySnapshot = firestore.collection("products")
                .orderBy(
                    "createdAt",
                    Query.Direction.DESCENDING
                ) // Urutkan berdasarkan waktu pembuatan (terbaru)
                .limit(10) // Batasi jumlah produk yang diambil (misalnya 10 produk)
                .get()
                .await()

            // Konversi hasil query ke daftar ProductModel
            querySnapshot.toObjects(ProductModel::class.java)
        } catch (e: Exception) {
            // Jika terjadi error, lempar exception atau kembalikan daftar kosong
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getProductById(productId: String): ProductModel {
        return try {
            // Ambil data produk berdasarkan ID dari Firestore
            val documentSnapshot =
                firestore.collection("products").document(productId).get().await()
            documentSnapshot.toObject(ProductModel::class.java)
                ?: throw Exception("Product not found")
        } catch (e: Exception) {
            // Jika terjadi error, lempar exception atau kembalikan produk kosong
            Timber.tag("ProductRepositoryImpl").e("Error fetching product by ID: $e")
            e.printStackTrace()
            ProductModel()
        }
    }


    private suspend fun uploadImagesToCloudinary(
        imagePaths: List<String>,
        category: String
    ): List<String> {
        return withContext(Dispatchers.IO) {
            imagePaths.map { path ->
                val uri = Uri.parse(path)
                val file = uriToFile(context, uri) // Konversi URI ke file sementara
                val uploadResult =
                    cloudinary.uploader().upload(
                        file,
                        mapOf("resource_type" to "image", "folder" to "products/$category")
                    )
                uploadResult["url"] as String
            }
        }
    }

}