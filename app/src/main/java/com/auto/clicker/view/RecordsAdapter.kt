package com.auto.clicker.view

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.auto.clicker.databinding.ItemRecordBinding
import com.auto.clicker.db.AppDatabase
import com.auto.clicker.db.Record
import com.auto.clicker.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecordsAdapter(val context: Context, private val records: MutableList<Record> = emptyList<Record>().toMutableList()) : RecyclerView.Adapter<RecordsAdapter.RecordViewHolder>() {

    private val lifecycleScope by lazy { (context as LifecycleOwner).lifecycleScope }

    init {
        loadAllRecords()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecordViewHolder(binding, binding.root)
    }

    override fun getItemCount(): Int = records.size

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        LogUtils.d("position = [${position}], adapterPosition = [${holder.adapterPosition}], layoutPosition = [${holder.layoutPosition}], position = [${holder.position}], oldPosition = [${holder.oldPosition}]")
        LogUtils.d("records: $records, name: ${records[holder.adapterPosition].name}")
        holder.binding.tvName.text = records[holder.adapterPosition].name
        LogUtils.d("tvName text ${records[holder.adapterPosition].name} set.")
        holder.binding.btnLoad.setOnClickListener {
            LogUtils.d("btnLoad clicked, position = [${position}], adapterPosition = [${holder.adapterPosition}], layoutPosition = [${holder.layoutPosition}], position = [${holder.position}], oldPosition = [${holder.oldPosition}]")
            loadRecord(holder.adapterPosition)
        }
        holder.binding.ivDelete.setOnClickListener {
            LogUtils.d("ivDelete clicked, position = [${position}], adapterPosition = [${holder.adapterPosition}], layoutPosition = [${holder.layoutPosition}], position = [${holder.position}], oldPosition = [${holder.oldPosition}]")
            deleteRecord(holder.adapterPosition)
        }
        LogUtils.d("onBindViewHolder complete.")
    }

    private fun loadAllRecords() {
        lifecycleScope.launch(Dispatchers.IO) {
            records.addAll(AppDatabase.INSTANCE.recordDao().loadAllRecords().reversed())
            LogUtils.d("records.size: ${records.size}, records: $records")
            withContext(Dispatchers.Main) {
                notifyItemRangeInserted(0, records.size)
                LogUtils.d("notifyItemRangeInserted: 0~${records.size}, records: $records")
            }
            LogUtils.d("loadAllRecords lifecycle complete")
        }
        LogUtils.d("loadAllRecords complete")
    }

    fun insertRecord(record: Record, insertCompleteListener: () -> Unit) {
        LogUtils.d("Prepare to insert Record: $record")
        if (records.any { it.name == record.name }) {
            LogUtils.d("A record with the name has already existed.")
            Toast.makeText(context, "A record with the name has already existed.", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            LogUtils.d("insert Record: $record")
            AppDatabase.INSTANCE.recordDao().insertRecord(record)
            withContext(Dispatchers.Main) {
                records.add(0, record)
                notifyItemInserted(0)
                insertCompleteListener.invoke()
            }
        }
    }

    private fun loadRecord(position: Int) {
        MyWindowManager.clearPoints(context)
        lifecycleScope.launch(Dispatchers.IO) {
            LogUtils.d("load: ${records[position]}")
            val record = AppDatabase.INSTANCE.recordDao().loadRecordByName(records[position].name)
            withContext(Dispatchers.Main) {
                MyWindowManager.addPointsByRecord(context, record)
            }
        }
    }

    private fun deleteRecord(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            LogUtils.d("delete record: ${records[position]}")
            AppDatabase.INSTANCE.recordDao().deleteRecordByName(records[position].name)
            withContext(Dispatchers.Main) {
                records.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    class RecordViewHolder(val binding: ItemRecordBinding, view: View) : RecyclerView.ViewHolder(view)
}
