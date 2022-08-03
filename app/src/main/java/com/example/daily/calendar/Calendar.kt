package com.example.daily.calendar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daily.R
import com.example.daily.calendar.adapter.CalendarGroupAdapter
import com.example.daily.calendar.data.entity.CalendarData
import com.example.daily.databinding.CalendarBinding
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView
import com.loper7.date_time_picker.DateTimeConfig
import com.loper7.date_time_picker.dialog.CardDatePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class Calendar : Fragment() {
    private var _binding: CalendarBinding? = null
    private val binding get() = _binding!!
    //日历选中的日期
    private lateinit var selectedCalendar: Calendar
    //存储日期
    private val calendarViewModel: CalendarViewModel by viewModels()
    private var groupAdapter: CalendarGroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarBinding.inflate(layoutInflater, container, false)
        groupAdapter = CalendarGroupAdapter(calendarViewModel)

        binding.calendarRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        val sp = requireContext().getSharedPreferences("main_store", Context.MODE_PRIVATE)
        if (sp.getBoolean("Calendar_isFirst", true)) {
            val date = SimpleDateFormat("yyyy-MM-dd").format(Date())
            val time = SimpleDateFormat("HH:mm").format(Date())
            calendarViewModel.apply {
                insertData(CalendarData(0, "点击右上角图标可切换日历视图", date, time, false))
                insertData(CalendarData(0, "点击以修改", date, time, false))
                insertData(CalendarData(0, "长按以删除", date, time, false))
            }
            sp.edit().putBoolean("Calendar_isFirst", false).apply()
        }

        calendarViewModel.allData.observe(viewLifecycleOwner) {
            reFresh()
        }

        calendarViewModel.getDifferentDate().observe(viewLifecycleOwner){
            calendarViewModel.updateMap(it)
        }

        calendarViewModel.map.observe(viewLifecycleOwner){
            binding.calendarview.setSchemeDate(it)
        }

        //右上角天数，点击切换日历视图
        binding.calendarDay.setOnClickListener {
            val layout = binding.calendarLayout
            if (layout.isExpand) {
                layout.shrink()
            } else {
                layout.expand()
            }
        }

        binding.calendarview.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: Calendar?) {}
            override fun onCalendarSelect(calendar: Calendar, isClick: Boolean) {
                reFresh()
            }
        })

        binding.calendarAdd.setOnClickListener {
            val mdate = Date(selectedCalendar.year-1900, selectedCalendar.month-1, selectedCalendar.day, Date().hours, Date().minutes)
            val newdata = CalendarData(0, "",
                SimpleDateFormat("yyyy-MM-dd").format(mdate),
                SimpleDateFormat("HH:mm").format(mdate), false
            )
            var input: EditText? = null
            MessageDialog.build().apply {
                setCustomView(object : OnBindView<MessageDialog>(R.layout.calendar_add) {
                    override fun onBind(dialog: MessageDialog?, v: View) {
                        val date = v.findViewById<TextView>(R.id.calendar_add_date)
                        val item1 = v.findViewById<LinearLayout>(R.id.calendar_add_item1)
                        input = v.findViewById(R.id.calendar_add_input)

                        date.text = "${newdata.date} ${newdata.time}"

                        item1.setOnClickListener {
                            val displayList = listOf(DateTimeConfig.MONTH, DateTimeConfig.DAY, DateTimeConfig.HOUR, DateTimeConfig.MIN).toMutableList()
                            CardDatePickerDialog.builder(requireContext()).apply {
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
                title = "添加日程"
                setOkButton("添加"){dialog, v ->
                    newdata.title = input?.text.toString()
                    dismiss()
                    if (newdata.title.isEmpty()) {
                        PopTip.show("日程不能为空")
                    } else {
                        PopTip.show("添加成功")
                        calendarViewModel.insertData(newdata)
                    }
                    true
                }
                cancelButton = "取消"
            }.show()
        }

        return binding.root
    }

    private fun reFresh() {
        selectedCalendar = binding.calendarview.selectedCalendar
        val mdate = Date(selectedCalendar.year-1900, selectedCalendar.month-1, selectedCalendar.day, Date().hours, Date().minutes)

        binding.calendarDay.text = mdate.date.toString()
        binding.calendarDate.text = SimpleDateFormat("yyyy年MM月").format(mdate)

        val empty = emptyList<CalendarData>()
        val list = arrayListOf(empty, empty)
        val type = arrayListOf("未完成", "已完成")

        val date = SimpleDateFormat("yyyy-MM-dd").format(mdate)
        //未完成
        calendarViewModel.searchDatabaseByDate(date, false).observe(viewLifecycleOwner){
            list[0] = it
            groupAdapter?.setData(type, list)
        }
        //已完成
        calendarViewModel.searchDatabaseByDate(date, true).observe(viewLifecycleOwner){
            list[1] = it
            groupAdapter?.setData(type, list)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}