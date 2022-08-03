package com.example.daily.calendar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daily.R
import com.example.daily.calendar.CalendarViewModel
import com.example.daily.calendar.data.entity.CalendarData
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.util.TextInfo

class CalendarGroupAdapter(private val calendarViewModel: CalendarViewModel): RecyclerView.Adapter<CalendarGroupAdapter.ViewHolder>() {

    private var dataList = emptyList<List<CalendarData>>()
    private var datatype = emptyList<String>()

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.calendar_cardview_title)
        val member = v.findViewById<RecyclerView>(R.id.calendar_cardview_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarGroupAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cardview, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = datatype[position]
        val itemAdapter = CalendarItemAdapter(calendarViewModel)
        holder.member.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
        itemAdapter.setData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(type: List<String>, list: List<List<CalendarData>>) {
        val newtype = ArrayList<String>()
        val newlist = ArrayList<List<CalendarData>>()
        for (i in 0..1) {
            if (list[i].isNotEmpty()) {
                newtype.add(type[i])
                newlist.add(list[i])
            }
        }
        dataList = newlist
        datatype = newtype
        notifyDataSetChanged()
    }
}