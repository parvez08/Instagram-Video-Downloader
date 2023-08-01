package com.example.instagramvideodownloader.work_manager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class CallLogUploadWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private var mContext = context

    override fun doWork(): Result {

        return Result.success()
    }
}