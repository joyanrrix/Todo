package com.example.daily.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.daily.R
import com.example.daily.calendar.CalendarViewModel
import com.example.daily.calendar.data.entity.CalendarData
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.util.TextInfo
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class CalendarItemAdapter(private val calendarViewModel: CalendarViewModel)
    : RecyclerView.Adapter<CalendarItemAdapter.ViewHolder>() {

    private var dataList = emptyList<CalendarData>()

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val item: LinearLayout = v.findViewById(R.id.calendar_item)
        val checkBox: CheckBox = v.findViewById(R.id.calendar_item_checkbox)
        val title: TextView = v.findViewById(R.id.calendar_item_title)
        val time: TextView = v.findViewById(R.id.calendar_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.calendar_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarItemAdapter.ViewHolder, position: Int) {
        holder.checkBox.isChecked = dataList[position].done
        holder.title.text = dataList[position].title
        holder.time.text = dataList[position].time

        if (holder.checkBox.isChecked) {
            holder.title.setTextColor(holder.itemView.resources.getColor(R.color.text_lunar))
            holder.time.setTextColor(holder.itemView.resources.getColor(R.color.text_lunar))
        }

        holder.item.setOnLongClickListener {
            MessageDialog.build().apply {
                title = "删除日程"
                message = "此操作会删除该日程，且无法撤回"
                setOkButton("删除") { baseDialog, v ->
                    calendarViewModel.deleteData(dataList[position])
                    dismiss()
                    PopTip.show("删除成功")
                    true
                }
                cancelButton = "取消"
                okTextInfo = TextInfo()
                    .setFontColor(Color.RED)
                    .setBold(true)

            }.show()
            true
        }

        holder.item.setOnClickListener {
            var input: EditText? = null
            val newdata = dataList[position]
            MessageDialog.build().apply {
                setCustomView(object : OnBindView<MessageDialog>(R.layout.calendar_add) {
                    override fun onBind(dialog: MessageDialog?, v: View) {
                        val date = v.findViewById<TextView>(R.id.calendar_add_date)
                        val item1 = v.findViewById<LinearLayout>(R.id.calendar_add_item1)
                        input = v.findViewById(R.id.calendar_add_input)

                        input?.hint = dataList[position].title

                        date.text = "${newdata.date} ${newdata.time}"

                        val mdate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date.text.toString())

                        item1.setOnClickListener {
                            val displayList = listOf(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN).toMutableList()
                            CardDatePickerDialog.builder(holder.itemView.context).apply {
                                setTitle("选择时间")
                                setDefaultTime(mdate.time)
                                setDisplayType(displayList)
                                showBackNow(false)
                                showFocusDateInfo(true)
                                setOnChoose { millisecond ->
                                    val calendar = android.icu.util.Calendar.getInstance()
                                    calendar.timeInMillis = millisecond
                                    val time = calendar.time
                                    newdata.date = SimpleDateFormat("yyyy-MM-dd").format(time)
                                    newdata.time = SimpleDateFormat("HH:mm").format(time)
                                    date.text = "${newdata.date} ${newdata.time}"
                                }
                            }.build().show()
                        }
                    }
                })
                title = "更新日程"
                setOkButton("更新"){dialog, v ->
                    newdata.title = input?.text.toString()
                    dismiss()
                    if (newdata.title.isEmpty()) {
                        PopTip.show("日程不能为空")
                    } else {
                        PopTip.show("更新成功")
                        calendarViewModel.updateData(newdata)
                    }
                    true
                }
                cancelButton = "取消"
            }.show()
        }

        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            dataList[position].done = isChecked
            calendarViewModel.updateData(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(list: List<CalendarData>) {
        val calendarDiffUtil = CalendarDiffUtil(dataList,list)
        val calendarDiffResult = DiffUtil.calculateDiff(calendarDiffUtil)
        dataList = list
        calendarDiffResult.dispatchUpdatesTo(this)
    }
}