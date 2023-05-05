package com.auto.clicker.view

import android.content.Context

import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.core.widget.doOnTextChanged
import com.auto.clicker.databinding.PointConfigBinding
import com.auto.clicker.db.PointConfig
import com.auto.clicker.utils.LogUtils

class PointConfigPopupWindow(context: Context, pointConfig: PointConfig) : PopupWindow() {
    var binding: PointConfigBinding

    init {
        binding = PointConfigBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        // Hardcode the width and height.
        width = 364
        height = 200
        // Set isFocusable as true so that the EditText can launch keyboard.
        isFocusable = true
        // Init delay times in editText
        binding.etTime.setText(pointConfig.delayMs.toString())
        binding.etTime.doOnTextChanged { text, start, before, count ->
            LogUtils.d("text = [${text}], start = [${start}], before = [${before}], count = [${count}]")
            // Change the delay time in real-time.
            if (text.isNullOrEmpty()) {
                pointConfig.delayMs = 0
            } else {
                pointConfig.delayMs = binding.etTime.text.toString().toLong()
            }
        }
    }
}