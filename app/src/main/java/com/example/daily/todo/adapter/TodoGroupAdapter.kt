package com.example.daily.todo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.daily.R
import com.example.daily.todo.Todo
import com.example.daily.todo.TodoViewModel
import com.example.daily.todo.data.entity.TodoData

class TodoGroupAdapter(private val todoViewModel: TodoViewModel) : RecyclerView.Adapter<TodoGroupAdapter.ViewHolder>() {

    private var dataList = emptyList<List<TodoData>>()
    private var itemType = emptyList<String>()

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val title = v.findViewById<TextView>(R.id.todo_cardview_title)
        val member = v.findViewById<RecyclerView>(R.id.todo_cardview_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoGroupAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoGroupAdapter.ViewHolder, position: Int) {
        val itemAdapter = TodoItemAdapter(todoViewModel)
        holder.member.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
        holder.title.text = itemType[position]
        itemAdapter.setData(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(type: List<String>, list: List<List<TodoData>>) {
        val newlist = ArrayList<List<TodoData>>()
        val newtype = ArrayList<String>()
        for (i in 0..1) {
            if (list[i].isNotEmpty()) {
                newtype.add(type[i])
                newlist.add(list[i])
            }
        }
        dataList = newlist
        itemType = newtype

        notifyDataSetChanged()
    }
}