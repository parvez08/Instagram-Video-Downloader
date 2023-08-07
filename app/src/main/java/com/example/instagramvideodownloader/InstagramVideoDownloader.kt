package com.example.instagramvideodownloader

import android.app.Application
import com.example.instagramvideodownloader.utils.firebaseDBModule
import com.example.instagramvideodownloader.utils.workManagerModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class InstagramVideoDownloader : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidContext(this@InstagramVideoDownloader)
            modules(workManagerModule, firebaseDBModule) // Add other modules if needed
        }

    }
}