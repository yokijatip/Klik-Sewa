package com.gity.kliksewa.di

import android.content.Context
import com.cloudinary.Cloudinary
import com.gity.kliksewa.domain.repository.BannerRepository
import com.gity.kliksewa.data.repository.BannerRepositoryImpl
import com.gity.kliksewa.data.repository.ProductRepositoryImpl
import com.gity.kliksewa.data.source.remote.BannerRemoteSource
import com.gity.kliksewa.domain.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideBannerRepository(
        remoteSource: BannerRemoteSource
    ): BannerRepository {
        return BannerRepositoryImpl(remoteSource)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        firestore: FirebaseFirestore,
        cloudinary: Cloudinary,
        context: Context
    ): ProductRepository {
        return ProductRepositoryImpl(firestore, cloudinary, context)
    }
}