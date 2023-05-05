package com.auto.clicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.PopupWindow
import com.auto.clicker.databinding.HelpPopupBinding
import com.auto.clicker.utils.ScreenUtils

class HelpPopupWindow(context: Context, attrs: AttributeSet? = null) : PopupWindow(context, attrs) {

    private var binding: HelpPopupBinding

    init {
        binding = HelpPopupBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        width = ScreenUtils.getScreenWidth() * 3 / 4
        isFocusable = true
    }
}