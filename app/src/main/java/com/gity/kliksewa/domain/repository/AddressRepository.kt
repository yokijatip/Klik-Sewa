package com.gity.kliksewa.domain.repository

import com.gity.kliksewa.data.model.AddressModel
import com.gity.kliksewa.util.Resource

interface AddressRepository {
    suspend fun getAddress(userId: String): Resource<List<AddressModel>>
    suspend fun addAddress(address: AddressModel): Resource<Boolean>
    suspend fun updateAddress(address: AddressModel): Resource<Boolean>
    suspend fun deleteAddress(addressId: String): Resource<Boolean>
}