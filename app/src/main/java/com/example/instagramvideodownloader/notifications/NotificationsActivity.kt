package com.example.instagramvideodownloader.notifications

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import com.example.instagramvideodownloader.databinding.ActivityNotificationsBinding

class NotificationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private var title: String = "Title default"
    private var text: String = " Text default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            ActivityNotificationsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        if (intent.extras?.containsKey("title")!! && intent.extras?.containsKey("text")!!) {
            title = intent.extras?.getString("title").toString()
            text = intent.extras?.getString("text").toString()
            onNotificationPosted()
        }

    }


    private fun onNotificationPosted() {

        // Update the TextViews with notification data
        runOnUiThread {
            binding.tvTitle.text = buildString {
                append("Notification Title: ")
                append(title)
            }
            binding.tvText.text = buildString {
                append("Notification Text: ")
                append(text)
            }
        }
    }
}