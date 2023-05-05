package com.auto.clicker

import android.content.Intent
import android.os.Bundle
import android.provider.Settings

import androidx.appcompat.app.AppCompatActivity
import com.auto.clicker.databinding.ActivityMainBinding
import com.auto.clicker.service.FloatWindowService
import com.auto.clicker.utils.AccessibilityUtils
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.view.MyWindowManager

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        LogUtils.d("MainActivity onCreate")
        binding.btnPermission.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        binding.ivHelp.setOnClickListener {
            MyWindowManager.showHelp(this, it)
        }
        binding.btnStart.setOnClickListener {
            LogUtils.d("btnStart clicked")
            startService(Intent(this, FloatWindowService::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (AccessibilityUtils.checkAccessibilityEnabled()) {
            binding.btnPermission.text = "Permission Granted √"
        } else {
            binding.btnPermission.text = "Request Permission"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d("MainActivity onDestroy")
    }
}