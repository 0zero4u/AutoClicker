package com.auto.clicker

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout

class FloatWindowView(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {
    lateinit var params: WindowManager.LayoutParams
    private var xInView = 0.0f
    private var yInView = 0.0f
    private var xInScreen = 0.0f
    private var yInScreen = 0.0f
    private val windowManager by lazy { context.getSystemService(Context.WINDOW_SERVICE) as WindowManager }

    init {
        LayoutInflater.from(context).inflate(R.layout.float_window, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("FloatWindowView#onTouchEvent", "event: $event")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                xInView = event.x
                yInView = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                xInScreen = event.rawX
                yInScreen = event.rawY
                updateViewPosition()
            }

        }
        return super.onTouchEvent(event)
    }

    fun updateViewPosition() {
        params.x = (xInScreen - xInView).toInt()
        params.y = (yInScreen - yInView).toInt()
        Log.d("~~~", "x: ${params.x}, y: ${params.y}, params: $params")
        windowManager.updateViewLayout(this, params)
    }
}