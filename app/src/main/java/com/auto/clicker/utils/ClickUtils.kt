package com.auto.clicker.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.graphics.Point

object ClickUtils {
    fun click(context: Context, point: Point) {
        if (context !is AccessibilityService) {
            LogUtils.d("The context isn't accessibilityService.")
            return
        }
        LogUtils.d("click: $point, accessibilityService: $context")
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(Path().apply { moveTo(point.x.toFloat(), point.y.toFloat()) }, 0, 200))
            .build()
        context.dispatchGesture(gesture, null, null)
    }
}