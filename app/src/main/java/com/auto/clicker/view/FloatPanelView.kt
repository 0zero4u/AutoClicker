package com.auto.clicker.view

import android.content.Context
import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import com.auto.clicker.R
import com.auto.clicker.databinding.FloatPanelBinding
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.utils.ScreenUtils

class FloatPanelView(context: Context, attrs: AttributeSet? = null) : LifecycleView(context, attrs) {
    private val binding: FloatPanelBinding
    lateinit var params: WindowManager.LayoutParams
    private var xInView = 0.0f
    private var yInView = 0.0f
    private var xInScreen = 0.0f
    private var yInScreen = 0.0f
    private val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    init {
        binding = FloatPanelBinding.inflate(LayoutInflater.from(context), this, true)
        binding.ivPlay.setOnClickListener {
            LogUtils.d("ivPlay clicked.")
            if (MyWindowManager.isPlay.value != true) {
                LogUtils.d("Start to play.")
                MyWindowManager.play(context)
            } else {
                LogUtils.d("Stop playing.")
                MyWindowManager.stop()
            }
        }
        binding.ivAdd.setOnClickListener {
            LogUtils.d("ivAdd clicked.")
            MyWindowManager.addPoint(context)
        }
        binding.ivRemove.setOnClickListener {
            LogUtils.d("ivRemove clicked.")
            MyWindowManager.removeLastPoint(context)
        }
        binding.ivSave.setOnClickListener {
            LogUtils.d("ivSave clicked.")
            MyWindowManager.showSavePopupWindow(context, binding.ivSave)
        }
        binding.ivClose.setOnClickListener {
            LogUtils.d("ivClose clicked.")
            MyWindowManager.close(context)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        observePlayState()
    }

    private fun observePlayState() {
        MyWindowManager.isPlay.observe(this) {
            LogUtils.d("Observed isPlay change: $it")
            binding.ivPlay.setImageResource(if (it) R.drawable.ic_stop else R.drawable.ic_play)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        LogUtils.d("onInterceptTouchEvent, event: $event")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Record the down position since the x,y in MotionEvent.ACTION_MOVE isn't the original click position
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
        }
        return super.onInterceptTouchEvent(event)
    }

    private fun updateViewPosition() {
        params.x = (xInScreen - xInView).toInt()
        params.y = (yInScreen - yInView).toInt()
        LogUtils.d("x: ${params.x}, y: ${params.y}")
        windowManager.updateViewLayout(this, params)
    }
}