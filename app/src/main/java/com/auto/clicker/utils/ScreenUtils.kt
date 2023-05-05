package com.auto.clicker.utils

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Point
import android.view.WindowManager
import com.auto.clicker.MyApplication

object ScreenUtils {
    private var point = Point()
    private var statusBarHeight = 0
    private val windowManager by lazy { MyApplication.application.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    /**
     * Return the weight of screen, in pixel.
     *
     * @return the weight of screen, in pixel
     */
    fun getScreenWidth(): Int {
        if (point.x != 0) return point.x
        windowManager.defaultDisplay.getRealSize(point)
        return point.x
    }

    /**
     * Return the height of screen, in pixel.
     *
     * @return the height of screen, in pixel
     */
    fun getScreenHeight(): Int {
        if (point.y != 0) return point.y
        windowManager.defaultDisplay.getRealSize(point)
        return point.y
    }

    fun getStatusBarHeight(): Int {
        if (statusBarHeight != 0) return statusBarHeight
        val resourceId = MyApplication.application.resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = MyApplication.application.resources.getDimensionPixelSize(resourceId)
        return statusBarHeight
    }

    fun isPortrait(): Boolean {
        return MyApplication.application.resources.configuration.orientation == ORIENTATION_PORTRAIT
    }

    fun isLandscape(): Boolean {
        return MyApplication.application.resources.configuration.orientation == ORIENTATION_LANDSCAPE
    }
}