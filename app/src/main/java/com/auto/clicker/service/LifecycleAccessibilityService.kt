package com.auto.clicker.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher

open class LifecycleAccessibilityService : AccessibilityService(), LifecycleOwner {

    private val serviceLifecycleDispatcher by lazy { ServiceLifecycleDispatcher(this) }

    override val lifecycle: Lifecycle
        get() = serviceLifecycleDispatcher.lifecycle

    override fun onCreate() {
        serviceLifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceLifecycleDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        serviceLifecycleDispatcher.onServicePreSuperOnStart()
        super.onServiceConnected()
    }

    override fun onDestroy() {
        serviceLifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }
}