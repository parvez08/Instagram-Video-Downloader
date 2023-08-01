package com.example.instagramvideodownloader.work_manager.models


data class CallLogItemModel(
    val phoneNumber: String,
    val callType: Int,
    val callDate: Long,
    val callDuration: Long
)