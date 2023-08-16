package org.madoldman.lockscreen

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.logging.Logger


class MainActivity : AppCompatActivity() {

    private lateinit var compName: ComponentName
    private lateinit var llTimePicker: LinearLayout
    private lateinit var timePicker: TimePicker
    private lateinit var tvStartTime:TextView
    private lateinit var tvEndTime:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.timePicker = findViewById(R.id.tpTime)
        this.llTimePicker = findViewById(R.id.llTimePicker)
        this.compName = ComponentName(this, DeviceAdmin::class.java)
        this.tvStartTime = findViewById(R.id.tvStartTime)
        this.tvEndTime = findViewById(R.id.tvEndTime)

        val sp: SharedPreferences = getSharedPreferences("time", Context.MODE_PRIVATE)
        val startTimeStr: String? = sp.getString("startTime", "22:00")
        val endTimeStr: String? = sp.getString("endTime", "05:00")
        if(startTimeStr != null){
            tvStartTime.text = startTimeStr
        }
        if(endTimeStr != null){
            tvEndTime.text = endTimeStr
        }


        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!")
        resultLauncher.launch(intent)


        val serviceIntent = Intent(this, LockService::class.java)
        startService(serviceIntent)
    }


    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode != Activity.RESULT_OK) {
            Toast.makeText (applicationContext, "register for activity result failed!" , Toast. LENGTH_SHORT ).show()
        }
    }

    fun save(view: View) {
        val sp:SharedPreferences = getSharedPreferences("time",Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor = sp.edit()
        editor.putString("startTime", tvStartTime.text.toString())
        editor.putString("endTime", tvEndTime.text.toString())
        editor.apply()
    }

    fun ok(view: View) {
        llTimePicker.visibility = View.GONE
    }

    fun pickTime(view: View) {
        timePicker.setOnTimeChangedListener(null)
        var txtView:TextView = findViewById(view.id)
        timePicker.setIs24HourView(true)
        timePicker.hour = Integer.valueOf(txtView.text.split(":")[0])
        timePicker.minute = Integer.valueOf(txtView.text.split(":")[1])
        llTimePicker.visibility = View.VISIBLE
        timePicker.setOnTimeChangedListener{_,hour,minute->
            val h = if(hour < 10) "0$hour" else hour
            val m = if(minute < 10) "0$minute" else minute
            txtView.text = "$h:$m"
        }
    }
}