package com.auto.clicker.service

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK

import android.view.accessibility.AccessibilityEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.auto.clicker.MainActivity
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.view.MyWindowManager

class FloatWindowService : LifecycleAccessibilityService(), LifecycleEventObserver {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d("onStartCommand")
        MyWindowManager.showPanel(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        LogUtils.d("onServiceConnected, automatically launch MainActivity to save the user's time.")
        // TODO: The memory should be released when the user turns off the permission.
        //  but I didn't find a way to detect when user turns off the permission of this app,
        //  so I have to release previous memory when a new connection is established.
        //  I hope I can find the right way to detect it some day.
        MyWindowManager.release(this)
        baseContext.startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        LogUtils.d("source = [${source}], event = [${event}]")
    }
}