package com.example.instagramvideodownloader.utils

import androidx.work.WorkManager
import com.google.firebase.database.FirebaseDatabase
import org.koin.dsl.module


val workManagerModule = module {
    single { WorkManager.getInstance(get()) }
}

val firebaseDBModule = module {
    single { FirebaseDatabase.getInstance() }
    single { FirebaseDatabase.getInstance().setPersistenceEnabled(true) }
}