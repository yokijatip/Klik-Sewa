package com.gity.kliksewa

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KlikSewaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        ChuckerInterceptor.Builder(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

}