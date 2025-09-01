package com.gity.kliksewa.data.source.remote

import com.gity.kliksewa.data.model.ml.PriceAnalysisRequest
import com.gity.kliksewa.data.model.ml.PriceAnalysisResponse
import com.gity.kliksewa.data.model.ml.PriceRecommendationRequest
import com.gity.kliksewa.data.model.ml.PriceRecommendationResponse
import com.gity.kliksewa.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MLRemoteDataSource @Inject constructor(
    private val apiService: MLApiService
) {
    fun getPriceRecommendation(request: PriceRecommendationRequest): Flow<Resource<PriceRecommendationResponse>> =
        flow {
            try {
                emit(Resource.Loading())
                Timber.tag("MLRemoteDataSource")
                    .d("Getting price recommendation for: ${request.name}")

                val response = apiService.getPriceRecommendation(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Timber.tag("MLRemoteDataSource")
                        .d("Price recommendation success: ${body.recommendedPriceDaily}")
                    emit(Resource.Success(body))
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid request data. Please check your input."
                        404 -> "Recommendation service not found."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to get price recommendation: ${response.message()}"
                    }
                    Timber.tag("MLRemoteDataSource").e("Price recommendation failed: $errorMessage")
                    emit(Resource.Error(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.SocketTimeoutException -> "Request timeout. Please check your connection."
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.ConnectException -> "Could not connect to recommendation service."
                    else -> "An error occurred: ${e.localizedMessage}"
                }
                Timber.tag("MLRemoteDataSource").e(e, "Exception getting price recommendation")
                emit(Resource.Error(errorMessage))
            }
        }.flowOn(Dispatchers.IO)

    fun analyzePricing(request: PriceAnalysisRequest): Flow<Resource<PriceAnalysisResponse>> =
        flow {
            try {
                emit(Resource.Loading())
                Timber.tag("MLRemoteDataSource")
                    .d("Analyzing price for: ${request.name} at ${request.current_price}")

                val response = apiService.analyzePricing(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    Timber.tag("MLRemoteDataSource")
                        .d("Price analysis success: ${body.recommendationStatus}")
                    emit(Resource.Success(body))
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid request data. Please check your input."
                        404 -> "Analysis service not found."
                        500 -> "Server error. Please try again later."
                        else -> "Failed to analyze price: ${response.message()}"
                    }
                    Timber.tag("MLRemoteDataSource").e("Price analysis failed: $errorMessage")
                    emit(Resource.Error(errorMessage))
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.SocketTimeoutException -> "Request timeout. Please check your connection."
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.ConnectException -> "Could not connect to analysis service."
                    else -> "An error occurred: ${e.localizedMessage}"
                }
                Timber.tag("MLRemoteDataSource").e(e, "Exception analyzing price")
                emit(Resource.Error(errorMessage))
            }
        }.flowOn(Dispatchers.IO)
}