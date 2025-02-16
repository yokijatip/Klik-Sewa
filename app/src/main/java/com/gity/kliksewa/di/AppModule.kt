package com.gity.kliksewa.di

import android.app.Application
import android.content.Context
import com.cloudinary.Cloudinary
import com.gity.kliksewa.data.repository.AuthRepositoryImpl
import com.gity.kliksewa.data.source.remote.FirebaseAuthSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuthSource {
        return FirebaseAuthSource()
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuthSource: FirebaseAuthSource): AuthRepositoryImpl {
        return AuthRepositoryImpl(firebaseAuthSource)
    }

    @Provides
    @Singleton
    fun provideCloudinary(): Cloudinary {
        val cloudName = "dhqpsn90p"
        val apiKey = "219489315573247"
        val apiSecret = "UsXscbAhfTZPJm1RraNbpDFv9ng"
        val cloudinaryUrl = "cloudinary://$apiKey:$apiSecret@$cloudName"
        return Cloudinary(cloudinaryUrl)
    }
}