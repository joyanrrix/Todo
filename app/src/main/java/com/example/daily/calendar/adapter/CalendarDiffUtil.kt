package com.example.daily.calendar.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.daily.calendar.data.entity.CalendarData

class CalendarDiffUtil(
    private val oldList: List<CalendarData>,
    private val newList: List<CalendarData>): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id &&
                oldItem.title == newItem.title &&
                oldItem.date == newItem.date &&
                oldItem.time == newItem.time
    }
}