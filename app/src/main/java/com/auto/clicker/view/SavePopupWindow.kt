package com.auto.clicker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.Toast
import com.auto.clicker.databinding.SavePopupBinding
import com.auto.clicker.db.Record
import com.auto.clicker.utils.LogUtils
import com.auto.clicker.utils.ScreenUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SavePopupWindow(context: Context, attrs: AttributeSet? = null) : PopupWindow(context, attrs) {

    private var binding: SavePopupBinding
    private var adapter: RecordsAdapter

    init {
        binding = SavePopupBinding.inflate(LayoutInflater.from(context))
        contentView = binding.root
        width = ScreenUtils.getScreenWidth() * 3 / 4
        height = ScreenUtils.getScreenHeight() / 2
        // Set isFocusable as true so that the EditText can launch keyboard.
        isFocusable = true
        adapter = RecordsAdapter(context)
        binding.rvRecords.layoutManager = WrapContentLinearLayoutManager(context)
        binding.rvRecords.adapter = adapter
        binding.btnSave.setOnClickListener {
            if (binding.etName.text.isNullOrEmpty()) {
                Toast.makeText(context, "The name cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (MyWindowManager.pointViews.isEmpty()) {
                Toast.makeText(context, "The points cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val record = Record(binding.etName.text.toString(), MyWindowManager.pointViews.map { it.pointConfig }.toMutableList())
            adapter.insertRecord(record) {
                LogUtils.d("insert complete, scroll to the first position")
                binding.rvRecords.smoothScrollToPosition(0)
            }
        }
    }

    fun updateName() {
        // Use the timestamp as default name.
        val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")
        val name = formatter.format(LocalDateTime.now())
        binding.etName.setText(name)
    }
}