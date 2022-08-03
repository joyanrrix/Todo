package com.example.daily.clock

import android.app.Application
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class ClockViewModel(application: Application) :  AndroidViewModel(application){

    val time: LiveData<Int> get() = _time
    private var _time = MutableLiveData<Int>()

    private lateinit var countDownTimer: CountDownTimer

    //初始化，使用分钟初始化
    fun setTime(data: Int) {
        _time.value = data * 60
    }

    //得到初始时间，返回秒
    fun getTime(): String? {
        val mtime = time.value!!
        val date = SimpleDateFormat("ss").parse(mtime.toString())
        var string: String? = null
        if (mtime > 3600) {
            string = SimpleDateFormat("HH:mm:ss").format(date)
        } else if (mtime > 60) {
            string = SimpleDateFormat("mm:ss").format(date)
        } else if (mtime > 0) {
            string = SimpleDateFormat("ss").format(date)
        }
        return string
    }

    fun startCountDown() {
        countDownTimer = object : CountDownTimer(time.value!!.times(1000).toLong(), 1000) {
            override fun onFinish() {}

            override fun onTick(p0: Long) {
                _time.value = p0.toInt() / 1000
            }
        }
        countDownTimer.start()
    }

    fun cancelCountDown() {
        countDownTimer.cancel()
    }
}