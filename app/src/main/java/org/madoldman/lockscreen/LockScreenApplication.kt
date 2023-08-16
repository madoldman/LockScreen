package org.madoldman.lockscreen

import android.app.Application
import android.content.Intent
import android.content.IntentFilter

class LockScreenApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val receiver = ScreenReceiver()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_USER_PRESENT)
        applicationContext.registerReceiver(receiver,filter)
    }
}