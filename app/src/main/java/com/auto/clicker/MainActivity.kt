package com.auto.clicker

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.auto.clicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val floatWindowLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnPermission.setOnClickListener {
            opFloatWindow()
        }
        binding.btnStart.setOnClickListener {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, FloatWindowService::class.java))
            } else {
                Toast.makeText(this, "Please ensure permission first.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun opFloatWindow() {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        intent.data = Uri.parse("package:$packageName")
        floatWindowLaunch.launch(intent)
    }
}