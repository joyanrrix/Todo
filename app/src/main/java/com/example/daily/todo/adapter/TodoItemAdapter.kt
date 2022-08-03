package com.example.daily.todo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.CaseMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.daily.R
import com.example.daily.todo.TodoViewModel
import com.example.daily.todo.data.entity.TodoData
import com.kongzue.dialogx.dialogs.InputDialog
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.util.TextInfo

class TodoItemAdapter(private val todoViewModel: TodoViewModel): RecyclerView.Adapter<TodoItemAdapter.ViewHolder>() {

    private var dataList = emptyList<TodoData>()

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val scheme = v.findViewById<View>(R.id.todo_item_scheme)
        val check = v.findViewById<CheckBox>(R.id.todo_item_check)
        val title = v.findViewById<TextView>(R.id.todo_item_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: TodoItemAdapter.ViewHolder, position: Int) {
        holder.title.text = dataList[position].title
        holder.check.isChecked = dataList[position].done

        if (holder.check.isChecked) {
            holder.title.setTextColor(holder.itemView.resources.getColor(R.color.text_lunar))
        }

        holder.scheme.background =
            holder.itemView.resources.getDrawable(when (dataList[position].scheme) {
            1 -> R.color.todo_scheme1
            2 -> R.color.todo_scheme2
            3 -> R.color.todo_scheme3
            else -> R.color.todo_scheme4
        })

        holder.check.setOnCheckedChangeListener{ buttonView, checked ->
            dataList[position].done = checked
            todoViewModel.updateData(dataList[position])
        }

        holder.title.setOnLongClickListener{
            MessageDialog.build().apply {
                title = "删除待办"
                message = "此操作会删除该待办，且无法撤回"
                setOkButton("删除") { baseDialog, v ->
                    todoViewModel.deleteData(dataList[position])
                    dismiss()
                    PopTip.show("删除成功")
                    true
                }
                cancelButton = "取消"
                okTextInfo = TextInfo().apply {
                    fontColor = Color.RED
                    isBold = true
                }

            }.show()
            true
        }

        holder.title.setOnClickListener {
            val newdata = dataList[position]
            var input: EditText? = null
            MessageDialog.build().apply {
                setCustomView(object : OnBindView<MessageDialog>(R.layout.todo_add){
                    override fun onBind(dialog: MessageDialog?, v1: View) {
                        input = v1.findViewById(R.id.todo_add_input)
                        input?.hint = newdata.title
                        v1.findViewById<ImageView>(R.id.todo_add_scheme).setImageDrawable(
                            v1.resources.getDrawable(when (newdata.scheme) {
                                1 -> R.drawable.ic_todo_scheme1
                                2 -> R.drawable.ic_todo_scheme2
                                3 -> R.drawable.ic_todo_scheme3
                                else -> R.drawable.ic_todo_scheme4
                            })
                        )
                        v1.findViewById<LinearLayout>(R.id.todo_add_item1).setOnClickListener {
                            MessageDialog.build().apply {
                                setCustomView(object : OnBindView<MessageDialog>(R.layout.todo_schemepicker){
                                    override fun onBind(dialog: MessageDialog?, v2: View) {
                                        val scheme = v1.findViewById<ImageView>(R.id.todo_add_scheme)
                                        v2.findViewById<LinearLayout>(R.id.todo_scheme1).setOnClickListener {
                                            newdata.scheme = 1
                                            scheme.setImageDrawable(resources.getDrawable(R.drawable.ic_todo_scheme1))
                                            dismiss()
                                        }
                                        v2.findViewById<LinearLayout>(R.id.todo_scheme2).setOnClickListener {
                                            newdata.scheme = 2
                                            scheme.setImageDrawable(resources.getDrawable(R.drawable.ic_todo_scheme2))
                                            dismiss()
                                        }
                                        v2.findViewById<LinearLayout>(R.id.todo_scheme3).setOnClickListener {
                                            newdata.scheme = 3
                                            scheme.setImageDrawable(resources.getDrawable(R.drawable.ic_todo_scheme3))
                                            dismiss()
                                        }
                                        v2.findViewById<LinearLayout>(R.id.todo_scheme4).setOnClickListener {
                                            newdata.scheme = 4
                                            scheme.setImageDrawable(resources.getDrawable(R.drawable.ic_todo_scheme4))
                                            dismiss()
                                        }
                                    }
                                })
                            }.show()
                        }
                    }
                })
                title = "更新待办"
                setOkButton("更新") {dialog, v ->
                    newdata.title = input?.text.toString()
                    dismiss()
                    if (newdata.title.isEmpty()) {
                        PopTip.show("待办不能为空")
                    } else {
                        PopTip.show("更新成功")
                        todoViewModel.updateData(newdata)
                    }
                    true
                }
                cancelButton = "取消"
                isCancelable = true
            }.show()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setData(list: List<TodoData>) {
        val todoDiffUtil = TodoDiffUtil(dataList, list)
        val todoDiffResult = DiffUtil.calculateDiff(todoDiffUtil)
        this.dataList = list
        todoDiffResult.dispatchUpdatesTo(this)
    }
}