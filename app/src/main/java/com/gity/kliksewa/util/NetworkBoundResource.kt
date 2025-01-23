package com.gity.kliksewa.util

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetworkBoundResource<ResultType, RequestType> {
    private val result = MutableLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.Loading()
        fetchFromNetwork()
    }

    private fun fetchFromNetwork() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiResponse = createCall()
                val processedData = processResponse(apiResponse)

                withContext(Dispatchers.Main) {
                    result.value = Resource.Success(processedData)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    result.value = Resource.Error(e.localizedMessage ?: "Terjadi kesalahan")
                }
            }
        }
    }
    protected abstract suspend fun createCall(): RequestType
    protected abstract suspend fun processResponse(response: RequestType): ResultType
}