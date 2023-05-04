package com.auto.clicker

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager

object MyWindowManager {
    fun createView(context: Context) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        params.format = PixelFormat.RGBA_8888
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL xor WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.gravity = Gravity.START xor Gravity.TOP
        params.width = 300
        params.height = 800
        val floatWindowView = FloatWindowView(context)
        floatWindowView.params = params
        windowManager.addView(floatWindowView, params)
    }
}