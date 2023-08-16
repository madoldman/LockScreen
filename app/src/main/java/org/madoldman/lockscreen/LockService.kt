package org.madoldman.lockscreen

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*
import java.util.logging.Logger

class LockService : Service() {
    private lateinit var deviceManger: DevicePolicyManager
    private lateinit var timer: Timer


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(Notification.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(1111111, notification)
        this.deviceManger = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val sp: SharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
                val startTimeStr: String = sp.getString("startTime", "22:00")!!
                val endTimeStr: String = sp.getString("endTime", "05:00")!!
                val startTime: Calendar = Calendar.getInstance();
                val endTime: Calendar = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTimeStr.split(":")[0]))
                startTime.set(Calendar.MINUTE, Integer.parseInt(startTimeStr.split(":")[1]))
                endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTimeStr.split(":")[0]))
                endTime.set(Calendar.MINUTE, Integer.parseInt(endTimeStr.split(":")[1]))
                if (startTime.timeInMillis >= endTime.timeInMillis) {
                    if (startTime.timeInMillis < System.currentTimeMillis() || endTime.timeInMillis > System.currentTimeMillis()) {
                        deviceManger.lockNow()
                    }
                } else {
                    if (startTime.timeInMillis < System.currentTimeMillis() && endTime.timeInMillis > System.currentTimeMillis()) {
                        deviceManger.lockNow()
                    }
                }

            }
        }, 0, 30000)
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}