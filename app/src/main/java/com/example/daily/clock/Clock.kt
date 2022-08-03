package com.example.daily.clock

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceActivity
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.daily.MainActivity
import com.example.daily.R
import com.example.daily.databinding.ClockBinding
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.interfaces.OnBindView
import java.text.SimpleDateFormat

class Clock : Fragment() {
    private var _binding: ClockBinding? = null
    private val binding get() = _binding!!
    private val clockViewModel: ClockViewModel by viewModels()
    private lateinit var sp: SharedPreferences
    private lateinit var service: NotificationManager

    private var restTime = 5
    private var number = 5
    private var autoRest = false
    private var finished = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ClockBinding.inflate(layoutInflater, container, false)

        init()

        //观察LiveData
        clockViewModel.time.observe(viewLifecycleOwner) {
            val time = SimpleDateFormat("ss").parse(it.toString())
            binding.clockProgressbar.progress = (binding.clockEdittext.text.toString().toInt()*60 - it).toFloat()

            if (it > 3600) {
                binding.clockTime.textSize = 40F
                binding.clockTime.text = SimpleDateFormat("HH:mm:ss").format(time)
            } else if (it > 60) {
                binding.clockTime.text = SimpleDateFormat("mm:ss").format(time)
            } else if (it > 0) {
                binding.clockTime.text = SimpleDateFormat("ss").format(time)
            }

            if (it == 0) {
                val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(2000)

                if (binding.clockTitle.text == "番茄计时中") {
                    finished++
                }

                if (autoRest) {
                    if (finished != number) {
                        if (binding.clockTitle.text == "休息中") {
                            startCountDown(binding.clockEdittext.text.toString().toInt())
                        } else {
                            restCountDown()
                        }
                    } else {
                        showFinishDialog()
                    }
                } else {
                    showFinishDialog()
                }
            }
        }

        //点击番茄钟设置
        binding.cloclSetting.setOnClickListener {
            var mNumber: EditText? = null
            var mRestTime: EditText? = null
            var mAutoRest: Switch? = null
            MessageDialog.build().apply {
                title = "番茄钟设置"
                setCustomView(object : OnBindView<MessageDialog>(R.layout.clock_setting) {
                    override fun onBind(dialog: MessageDialog?, v: View) {
                        mNumber = v.findViewById(R.id.clock_setting_number)
                        mRestTime = v.findViewById(R.id.clock_setting_rest)
                        mAutoRest = v.findViewById(R.id.clock_setting_switch)

                        mNumber!!.hint = number.toString()
                        mRestTime!!.hint = restTime.toString()
                        mAutoRest!!.isChecked = autoRest
                    }
                })
                setOkButton("确认") {dialog, view ->
                    var temp1 = mNumber!!.hint.toString().toInt()
                    var temp2 = mRestTime!!.hint.toString().toInt()
                    var flag = true

                    if (mNumber!!.text.isNotEmpty()) {
                        temp1 = mNumber!!.text.toString().toInt()
                    }
                    if (mRestTime!!.text.isNotEmpty()) {
                        if (mRestTime!!.text.toString().toInt() > binding.clockEdittext.text.toString().toInt()) {
                            PopTip.show("休息时间比专注时间都长啦！")
                            flag = false
                        } else {
                            temp2 = mRestTime!!.text.toString().toInt()
                        }
                    }
                    if (flag) {
                        dismiss()
                        number = temp1
                        restTime = temp2
                        autoRest = mAutoRest!!.isChecked
                        PopTip.show("设置成功")
                    }
                    true
                }
                cancelButton = "取消"
            }.show()
        }

        //点击开始专注按钮
        binding.clockStart.setOnClickListener {
            val input = binding.clockEdittext.text.toString().toInt()
            if (input > 480) {
                PopTip.show("单次专注时间不超过8小时")
            } else if (input == 0) {
                PopTip.show("不能设置0分钟")
            }else {
                startCountDown(input)
            }
        }

        //点击暂停按钮
        binding.clockPause.setOnClickListener {
            pauseCountDown()
        }

        //点击继续按钮
        binding.clockCount.setOnClickListener {
            startCountDown()
        }

        //点击放弃按钮
        binding.clockStop.setOnClickListener {
            pauseCountDown()
            createNotification(1, "专注暂停中")
            MessageDialog.build().apply {
                title = "确定放弃专注？"
                setOkButton("放弃") { dialog, view ->
                    dismiss()
                    stopCountDown()
                    true
                }
                setCancelButton("取消") { dialog, view ->
                    startCountDown()
                    dismiss()
                    true
                }
                isCancelable = false
            }.show()
        }

        return binding.root
    }

    private fun init() {
        sp = requireActivity().getSharedPreferences("main_store", Context.MODE_PRIVATE)
        val channel = NotificationChannel("clock-channel", "专注通知", NotificationManager.IMPORTANCE_NONE)
        service = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)

        if (NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            && sp.getBoolean("Clock_isFirst", true)) {
            MessageDialog.build().apply {
                title = "是否允许通知"
                setOkButton("允许") {dialog, view ->
                    dismiss()
                    requireActivity().intent
                    val intent = Intent().apply {
                        action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().applicationInfo.packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, "clock-channel")
                    }
                    startActivity(intent)
                    showTip()
                    true
                }
                setCancelButton("拒绝") {dialog, view ->
                    dismiss()
                    showTip()
                    true
                }
                isCancelable = false
            }.show()
        }
    }

    //使用提示
    private fun showTip() {
        sp.edit().putBoolean("Clock_isFirst", false).apply()
        MessageDialog.build().apply {
            title = "使用指南"
            message = "点击中间的数字可以进行修改"
            okButton = "确认"
        }.show()
    }

    //计时结束对话框
    private fun showFinishDialog() {
        if (finished != number) {
            if (binding.clockTitle.text == "番茄计时中") {
                MessageDialog.build().apply {
                    title = "已完成${finished}个番茄钟" + '\n' + "休息一下吧~"
                    setOkButton("开始休息") { dialog, view ->
                        dismiss()
                        restCountDown()
                        true
                    }
                    setOtherButton("跳过休息") { dialog, view ->
                        dismiss()
                        startCountDown(binding.clockEdittext.text.toString().toInt())
                        true
                    }
                    setCancelButton("结束") { dialog, view ->
                        dismiss()
                        stopCountDown()
                        requireContext().stopService(Intent(requireContext(), ClockService::class.java))
                        createNotification(2, "专注已结束")
                        true
                    }
                    isCancelable = false
                    buttonOrientation = LinearLayout.VERTICAL
                }.show()
            } else if (binding.clockTitle.text == "休息中") {
                MessageDialog.build().apply {
                    title = "休息结束~"
                    setOkButton("开始专注") { dialog, view ->
                        startCountDown(restTime)
                        dismiss()
                        true
                    }
                    setCancelButton("结束") { dialog, view ->
                        dismiss()
                        requireContext().stopService(Intent(requireContext(), ClockService::class.java))
                        createNotification(2, "专注已结束")
                        stopCountDown()
                        true
                    }
                    isCancelable = false
                    buttonOrientation = LinearLayout.VERTICAL
                }.show()
            }
        } else {
            MessageDialog.build().apply {
                title = "已完成所有番茄钟！"
                setOkButton("确认") { dialog, view ->
                    stopCountDown()
                    requireContext().stopService(Intent(requireContext(), ClockService::class.java))
                    createNotification(2, "专注已结束")
                    dismiss()
                    true
                }
            }.show()
        }
    }

    //发送通知
    private fun createNotification(id: Int, title: String) {
        val notification = Notification.Builder(requireContext(), "clock-channel")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_main_clock)
            .build()

        service.notify(id, notification)
    }

    //开始计时
    private fun startCountDown(time: Int? = null) {
        startSituation()
        if (time != null) {
            clockViewModel.setTime(time)
            binding.clockProgressbar.apply {
                progress = 0f
                progressMax = (time * 60).toFloat()
            }
        }
        requireActivity().startService(Intent(requireContext(), ClockService::class.java))
        clockViewModel.startCountDown()
    }

    //暂停计时
    private fun pauseCountDown() {
        pauseSituation()
        createNotification(1, "计时暂停中")
        clockViewModel.cancelCountDown()
    }

    //休息计时
    private fun restCountDown() {
        restSituation()
        createNotification(1, "正在休息中")
        clockViewModel.setTime(restTime)
        binding.clockProgressbar.apply {
            progress = 0f
            progressMax = (restTime * 60).toFloat()
        }
        clockViewModel.startCountDown()
    }

    //停止计时
    private fun stopCountDown() {
        finished = 0
        stopSituation()
        requireContext().stopService(Intent(requireContext(), ClockService::class.java))
        clockViewModel.setTime(binding.clockEdittext.text.toString().toInt())
        clockViewModel.cancelCountDown()
    }

    //停止计时的界面状态
    private fun stopSituation() {
        binding.clockPause.visibility = View.GONE
        binding.clockStop.visibility = View.GONE
        binding.clockStart.visibility = View.VISIBLE
        binding.clockCount.visibility = View.GONE
        binding.clockTime.visibility = View.GONE
        binding.clockEdittext.visibility = View.VISIBLE
        binding.clockTitle.text = null
    }

    //暂停计时的界面状态
    private fun pauseSituation() {
        binding.clockPause.visibility = View.GONE
        binding.clockStop.visibility = View.VISIBLE
        binding.clockStart.visibility = View.GONE
        binding.clockCount.visibility = View.VISIBLE
        binding.clockTime.visibility = View.VISIBLE
        binding.clockEdittext.visibility = View.GONE
        binding.clockTitle.text = "暂停中"
    }

    //开始计时的界面状态
    private fun startSituation() {
        binding.clockPause.visibility = View.VISIBLE
        binding.clockStop.visibility = View.VISIBLE
        binding.clockStart.visibility = View.GONE
        binding.clockCount.visibility = View.GONE
        binding.clockTime.visibility = View.VISIBLE
        binding.clockEdittext.visibility = View.GONE
        binding.clockTitle.text = "番茄计时中"
    }

    //休息计时的界面状态
    private fun restSituation() {
        binding.clockPause.visibility = View.VISIBLE
        binding.clockStop.visibility = View.VISIBLE
        binding.clockStart.visibility = View.GONE
        binding.clockCount.visibility = View.GONE
        binding.clockTime.visibility = View.VISIBLE
        binding.clockEdittext.visibility = View.GONE
        binding.clockTitle.text = "休息中"
    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().stopService(Intent(requireContext(), ClockService::class.java))
        _binding = null
    }
}