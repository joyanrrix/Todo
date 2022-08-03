package com.example.daily.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daily.R
import com.example.daily.databinding.TodoBinding
import com.example.daily.todo.adapter.TodoGroupAdapter
import com.example.daily.todo.data.entity.TodoData
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView

class Todo: Fragment() {
    private val binding get() = _binding!!
    private var _binding: TodoBinding? = null

    private var groupAdapter: TodoGroupAdapter? = null

    private val todoViewModel: TodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TodoBinding.inflate(layoutInflater, container, false)
        groupAdapter = TodoGroupAdapter(todoViewModel)

        binding.todoRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        val sp = requireContext().getSharedPreferences("main_store", Context.MODE_PRIVATE)
        if (sp.getBoolean("Todo_isFirst", true)) {
            todoViewModel.apply {
                insertData(TodoData(0, "点击以修改", 1, false))
                insertData(TodoData(0, "长按以删除", 1, false))
            }
            sp.edit().putBoolean("Todo_isFirst", false).apply()
        }

        reFresh()

        binding.todoAdd.setOnClickListener {
            val newdata = TodoData(0, "", 4, false)
            var input: EditText? = null
            MessageDialog.build().apply {
                setCustomView(object : OnBindView<MessageDialog>(R.layout.todo_add){
                    override fun onBind(dialog: MessageDialog?, v1: View) {
                        input = v1.findViewById(R.id.todo_add_input)
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
                title = "添加待办"
                setOkButton("添加") {dialog, v ->
                    newdata.title = input?.text.toString()
                    dismiss()
                    if (newdata.title.isEmpty()) {
                        PopTip.show("待办不能为空")
                    } else {
                        PopTip.show("添加成功")
                        todoViewModel.insertData(newdata)
                    }
                    true
                }
                cancelButton = "取消"
                isCancelable = true
            }.show()
        }

        return binding.root
    }

    private fun reFresh() {
        val empty = emptyList<TodoData>()
        val list = arrayListOf(empty, empty)
        val type = arrayListOf("待办", "已完成")
        //未完成
        todoViewModel.searchData(false).observe(viewLifecycleOwner) {
            list[0] = it
            groupAdapter?.setData(type.toList(), list.toList())
        }
        //已完成
        todoViewModel.searchData(true).observe(viewLifecycleOwner) {
            list[1] = it
            groupAdapter?.setData(type.toList(), list.toList())
        }
    }
}