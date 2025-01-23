package com.gity.kliksewa.di

import com.gity.kliksewa.data.repository.AuthRepository
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
    fun provideAuthRepository(firebaseAuthSource: FirebaseAuthSource): AuthRepository {
        return AuthRepository(firebaseAuthSource)
    }
}