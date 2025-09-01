package com.gity.kliksewa.di

import com.gity.kliksewa.data.repository.MLRepositoryImpl
import com.gity.kliksewa.data.source.remote.MLApiService
import com.gity.kliksewa.domain.repository.MLRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MLModule {

    @Provides
    @Named("ml_base_url")
    fun provideMLBaseUrl(): String {
        // Ganti dengan URL ML API Anda
        return "https://your-ml-api-url.com/"
    }

    @Provides
    @Singleton
    @Named("ml_retrofit")
    fun provideMLRetrofit(@Named("ml_base_url") baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMLApiService(@Named("ml_retrofit") retrofit: Retrofit): MLApiService {
        return retrofit.create(MLApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMLRepository(mlRepositoryImpl: MLRepositoryImpl): MLRepository {
        return mlRepositoryImpl
    }
}