package com.example.daily.todo.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.daily.todo.data.entity.TodoData

class TodoDiffUtil(
    private val oldList: List<TodoData>,
    private val newList: List<TodoData>): DiffUtil.Callback() {
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
                    oldItem.done == newItem.done
        }
}