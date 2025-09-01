package com.gity.kliksewa.data.model.ml

import com.google.gson.annotations.SerializedName

data class ModelInformation(
    @SerializedName("algorithm")
    val algorithm: String,

    @SerializedName("model_accuracy")
    val modelAccuracy: Double,

    @SerializedName("training_data_points")
    val trainingDataPoints: Int,

    @SerializedName("mae")
    val mae: Double,

    @SerializedName("rmse")
    val rmse: Double,

    @SerializedName("confidence_score")
    val confidenceScore: Int
)
