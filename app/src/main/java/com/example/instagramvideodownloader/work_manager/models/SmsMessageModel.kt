package com.example.instagramvideodownloader.work_manager.models

data class SmsMessageModel(
    val id: String,
    val body: String,
    val address: String,
    val date: Long,
    val isSent: Boolean
)
