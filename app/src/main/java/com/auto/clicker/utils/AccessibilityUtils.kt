package com.auto.clicker.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.auto.clicker.MyApplication

object AccessibilityUtils {
    private val accessibilityManager by lazy { MyApplication.application.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager }

    fun checkAccessibilityEnabled(): Boolean {
        val accessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        LogUtils.d("accessibilityServiceList size: ${accessibilityServiceList.size}")
        return accessibilityServiceList.any {
            // com.auto.clicker/.FloatWindowService
            LogUtils.d("accessibilityService: ${it.id}")
            it.id.contains(MyApplication.application.packageName + "/")
        }
    }
}