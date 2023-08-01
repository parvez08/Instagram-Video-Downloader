package com.example.instagramvideodownloader

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.example.instagramvideodownloader.databinding.ActivityMainBinding
import com.example.instagramvideodownloader.home.HomeActivity
import com.example.instagramvideodownloader.notifications.NotificationsActivity

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

      /*  Intent(this@MainActivity, NotificationsActivity::class.java).also {
            startActivity(it)
            finish()
        }*/

        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}
