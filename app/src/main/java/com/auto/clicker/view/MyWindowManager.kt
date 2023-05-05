package com.auto.clicker.view

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.auto.clicker.constants.POINT_CLICK_DELAY
import com.auto.clicker.db.PointConfig
import com.auto.clicker.db.Record
import com.auto.clicker.service.FloatWindowService
import com.auto.clicker.utils.AccessibilityUtils
import com.auto.clicker.utils.ClickUtils
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.utils.ScreenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CopyOnWriteArrayList

object MyWindowManager {
    private var floatPanelView: FloatPanelView? = null
    private var savePopupWindow: SavePopupWindow? = null
    private lateinit var helpPopupWindow: HelpPopupWindow

    // Use CopyOnWriteArrayList to avoid ConcurrentModificationException, such as user clicks close panel when it's playing.
    val pointViews = CopyOnWriteArrayList<PointView>()

    val isPlay = MutableLiveData(false)

    fun showPanel(context: Context) {
        if (!AccessibilityUtils.checkAccessibilityEnabled()) {
            LogUtils.d("no Accessibility permission")
            Toast.makeText(context, "Please grant the permission.", Toast.LENGTH_SHORT).show()
            return
        }
        val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }
        if (floatPanelView != null) {
            LogUtils.d("floatPanelView has been initialized")
            if (floatPanelView?.isAttachedToWindow == true) {
                LogUtils.d("floatPanelView has been attached")
            } else {
                LogUtils.d("floatPanelView hasn't been attached")
                windowManager.addView(floatPanelView, floatPanelView?.params)
            }
            return
        }
        LogUtils.d("init floatPanelView")
        floatPanelView = FloatPanelView(context)
        floatPanelView?.params = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY).apply {
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.START or Gravity.TOP
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            // Set it as 1/4 in vertical
            y = ScreenUtils.getScreenHeight() / 4
        }
        LogUtils.d("params.y: ${floatPanelView?.params?.y}")
        windowManager.addView(floatPanelView, floatPanelView?.params)
    }

    fun close(context: Context) {
        release(context)
        // Stop Service
        context.stopService(Intent(context, FloatWindowService::class.java))
    }

    fun clearPoints(context: Context) {
        LogUtils.d("clearPoints. ${pointViews.size}")
        if (pointViews.isEmpty()) return
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        pointViews.forEach {
            LogUtils.d("remove: ${it.number}")
            windowManager.removeView(it)
        }
        pointViews.clear()
        LogUtils.d("pointViews cleared")
    }

    fun play(context: Context) {
        if (context !is LifecycleOwner) {
            LogUtils.d("context isn't lifecycleOwner")
            return
        }
        if (pointViews.isEmpty()) {
            LogUtils.d("There's no point, cannot play.")
            Toast.makeText(context, "There's no point, cannot play.", Toast.LENGTH_SHORT).show()
            return
        }
        LogUtils.d("Start to play.")
        isPlay.postValue(true)
        context.lifecycleScope.launch(Dispatchers.IO) {
            pointViews.forEach {
                // Change the layoutParams to add FLAG_NOT_TOUCHABLE so that it won't intercept the click event.
                it.params.value?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                it.updateViewLayout()
            }
            // Delay some time so that the view layout can be updated to untouchable completely.
            delay(POINT_CLICK_DELAY)
            while (isPlay.value == true) {
                pointViews.forEach {
                    // Interrupt immediately
                    if (isPlay.value != true) return@forEach
                    // TODO: Display a shot anim might be better
                    withContext(Dispatchers.Main) {
                        it.bright()
                    }
                    // Set a min value as 50ms, in case the click is too fast.
                    // I think it's helpful in some cases, such as to avoid the loop be executed to fast, but I'm not sure if it's a good design.
                    delay(50 + it.pointConfig.delayMs)
                    ClickUtils.click(context, it.centerPoint())
                    withContext(Dispatchers.Main) {
                        it.dark()
                    }
                }
            }
            pointViews.forEach {
                // Restore the layoutParams to remove FLAG_NOT_TOUCHABLE so that the pointView can be touched and moved normally.
                it.params.value?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                it.updateViewLayout()
            }
        }
    }

    fun stop() {
        if (isPlay.value == true) {
            LogUtils.d("It's playing, stop it.")
            isPlay.postValue(false)
        } else {
            LogUtils.d("It's not started yet.")
        }
    }

    fun addPoint(context: Context) {
        if (isPlay.value == true) {
            LogUtils.d("It's playing, adding point at this time isn't allowed.")
            Toast.makeText(context, "It's playing, adding point at this time isn't allowed.", Toast.LENGTH_SHORT).show()
            return
        }
        LogUtils.d("addPoint")
        addPointByPointConfig(context, PointConfig(ScreenUtils.getScreenWidth() / 2, ScreenUtils.getScreenHeight() / 2, 200))
    }

    private fun addPointByPointConfig(context: Context, pointConfig: PointConfig) {
        val pointView = PointView(context, pointConfig = pointConfig)
        val pointLayoutParams = WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY).apply {
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.START or Gravity.TOP
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            x = pointConfig.x
            y = pointConfig.y
            LogUtils.d("x: $x, y: $y")
        }
        pointView.params.postValue(pointLayoutParams)
        // The index in point
        pointView.number.value = pointViews.size + 1
        pointViews.add(pointView)
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(pointView, pointLayoutParams)
    }

    fun addPointsByRecord(context: Context, record: Record) {
        LogUtils.d("addPointsByRecord, pointConfigs in record size: ${record.pointConfigs.size}")
        addPointsByPointConfigs(context, record.pointConfigs)
    }

    private fun addPointsByPointConfigs(context: Context, pointConfigs: List<PointConfig>) {
        pointConfigs.forEach {
            addPointByPointConfig(context, it)
        }
    }

    fun removeLastPoint(context: Context) {
        if (isPlay.value == true) {
            LogUtils.d("It's playing, removing point at this time isn't allowed.")
            Toast.makeText(context, "It's playing, removing point at this time isn't allowed.", Toast.LENGTH_SHORT).show()
            return
        }
        if (pointViews.isEmpty()) {
            LogUtils.d("There's no pointView.")
            return
        }
        val pointView = pointViews.removeLast()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(pointView)
    }

    fun showPointConfigPopupWindow(pointView: PointView) {
        LogUtils.d("Show point config popupWindow: ${pointView.pointConfig}")
        // Dismiss other showing popupWindows
        pointViews.forEach {
            if (it != pointView && it.pointConfigPopupWindow?.isShowing == true) {
                // FIXME: After the popupWindow dismissed, the PopupDecorView in the popupWindow will still intercept the touch event, which caused an issue:
                //  Reproduce steps:
                //  1. Click a point to launch its pointConfigPopupWindow
                //  2. Click another point to launch its pointConfigPopupWindow, verify the pointConfigPopupWindow in step 1 be closed correctly.
                //  3. Click the point in step 1, verify its pointConfigPopupWindow can't be launched.
                //  Unfortunately I haven't found a way to fix it. But the pointConfigPopupWindow can be launched normally at the next click, so it's not a big issue.
                it.pointConfigPopupWindow?.dismiss()
            }
        }
        if (pointView.pointConfigPopupWindow == null) {
            LogUtils.d("Init point config popupWindow: ${pointView.pointConfig}")
            pointView.pointConfigPopupWindow = PointConfigPopupWindow(pointView.context, pointView.pointConfig)
        }
        if (pointView.pointConfigPopupWindow!!.isShowing) {
            LogUtils.d("It's already shown.")
            return
        }
        pointView.pointConfigPopupWindow!!.showAsDropDown(pointView, -(pointView.pointConfigPopupWindow!!.width - pointView.width) / 2, 16)
    }

    fun showSavePopupWindow(context: Context, ivSave: AppCompatImageView) {
        if (isPlay.value == true) {
            LogUtils.d("It's playing, showing save popup window at this time isn't allowed.")
            Toast.makeText(context, "It's playing, showing save popup window at this time isn't allowed.", Toast.LENGTH_SHORT).show()
            return
        }
        LogUtils.d("Show SavePopupWindow")
        if (savePopupWindow == null) {
            LogUtils.d("init savePopupWindow")
            savePopupWindow = SavePopupWindow(context)
        }
        if (savePopupWindow?.isShowing == true) {
            LogUtils.d("It's already shown.")
            return
        }
        savePopupWindow?.updateName()
        savePopupWindow?.showAtLocation(ivSave, Gravity.CENTER, 0, 0)
    }

    fun showHelp(context: Context, view: View) {
        LogUtils.d("Show HelpPopupWindow")
        if (!::helpPopupWindow.isInitialized) {
            LogUtils.d("init helpPopupWindow")
            helpPopupWindow = HelpPopupWindow(context)
        }
        if (helpPopupWindow.isShowing) {
            LogUtils.d("It's already shown.")
            return
        }
        helpPopupWindow.showAsDropDown(view, -(helpPopupWindow.width - view.width) / 2, 24)
    }

    fun release(context: Context) {
        LogUtils.d("release")
        // Stop if it's running
        stop()
        LogUtils.d("floatPanelView?.isAttachedToWindow: ${floatPanelView?.isAttachedToWindow}, savePopupWindow?.isShowing: ${savePopupWindow?.isShowing}")
        if (floatPanelView != null) {
            if (floatPanelView?.isAttachedToWindow == true) {
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                windowManager.removeView(floatPanelView)
            }
            floatPanelView = null
        }
        if (savePopupWindow != null) {
            if (savePopupWindow?.isShowing == true) {
                savePopupWindow?.dismiss()
            }
            savePopupWindow = null
        }
        // Clear all pointViews
        clearPoints(context)
    }
}