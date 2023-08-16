package org.madoldman.lockscreen

import android.app.Activity
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.logging.Logger

class ScreenReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if(Intent.ACTION_USER_PRESENT == action){
            if(!isServiceRunning(context,LockService::class.java)){
                val serviceIntent = Intent(context, LockService::class.java)
                context.startForegroundService(serviceIntent)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun <T> isServiceRunning(context: Context,service: Class<T>): Boolean {
        return (context.getSystemService(Activity.ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE)
            .any { it.service.className == service.name }
    }
}