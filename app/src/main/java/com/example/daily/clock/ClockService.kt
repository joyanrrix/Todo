package com.example.daily.clock

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.daily.MainActivity
import com.example.daily.R
import java.text.SimpleDateFormat
import java.util.EnumSet.of

class ClockService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channel = NotificationChannel("clock-channel", "专注通知",
            NotificationManager.IMPORTANCE_NONE
        )

        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)

        val notification: Notification = Notification.Builder(this, "clock-channel")
            .setContentTitle("正在专注中")
            .setSmallIcon(R.drawable.ic_main_clock)
            .build()

        startForeground(1, notification)

        return super.onStartCommand(intent, flags, startId)
    }
}