package com.auto.clicker.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.lifecycle.MutableLiveData
import com.auto.clicker.R
import com.auto.clicker.databinding.PointBinding
import com.auto.clicker.db.PointConfig
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.utils.ScreenUtils

class PointView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, val pointConfig: PointConfig = PointConfig(0, 0, 200)) : LifecycleView(context, attrs) {
    var pointBinding: PointBinding
    var number = MutableLiveData(0)
    var params = MutableLiveData<WindowManager.LayoutParams>()
    var pointConfigPopupWindow: PopupWindow? = null
    private var xInView = 0.0f
    private var yInView = 0.0f
    private var xInScreen = 0.0f
    private var yInScreen = 0.0f
    private val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    init {
        pointBinding = PointBinding.inflate(LayoutInflater.from(context), this, true)
        number.observe(this) {
            pointBinding.tvPoint.text = it.toString()
        }
        params.observe(this) {
            pointConfig.x = it.x
            pointConfig.y = it.y
            windowManager.updateViewLayout(this, it)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        LogUtils.d("x: ${event?.x},y: ${event?.y},rawX: ${event?.rawX},rawY: ${event?.rawY}, event: $event")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                xInView = event.x
                yInView = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                xInScreen = event.rawX
                yInScreen = event.rawY
                if (ScreenUtils.isPortrait()) {
                    yInScreen -= ScreenUtils.getStatusBarHeight()
                } else {
                    xInScreen -= ScreenUtils.getStatusBarHeight()
                }
                updateViewPosition()
            }

            MotionEvent.ACTION_UP -> {
                if (event.x == xInView && event.y == yInView) {
                    // Clicked
                    LogUtils.d("Point clicked.")
                    MyWindowManager.showPointConfigPopupWindow(this)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updateViewPosition() {
        params.value?.x = (xInScreen - xInView).toInt()
        params.value?.y = (yInScreen - yInView).toInt()
        updateViewLayout()
        LogUtils.d("x: ${params.value?.x}, y: ${params.value?.y}")
    }

    fun updateViewLayout() {
        params.postValue(params.value)
    }

    fun centerPoint(): Point {
        return Point(params.value!!.x + width / 2, params.value!!.y + ScreenUtils.getStatusBarHeight() + height / 2)
    }

    fun bright() {
        pointBinding.tvPoint.setBackgroundResource(R.drawable.ic_bright_point)
        pointBinding.tvPoint.setTextColor(context.getColor(R.color.red))
    }

    fun dark() {
        pointBinding.tvPoint.setBackgroundResource(R.drawable.ic_point)
        pointBinding.tvPoint.setTextColor(context.getColor(R.color.black))
    }
}