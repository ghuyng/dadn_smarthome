package com.example.smarthome

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class SetTurnOnOffTimeActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    private lateinit var tvTimer1 : TextView
    private lateinit var tvTimer2 : TextView
    private lateinit var confirm  : TextView
    private lateinit var stateButton : SwitchCompat

    private lateinit var device :Device
    private lateinit var deviceTurnOnTime : Date
    private lateinit var deviceTurnOffTime : Date
    @SuppressLint("CutPasteId", "SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_turn_on_off_time)
        findViewById<ImageButton>(R.id.setting_time_back_button).setOnClickListener { onBackPressed() }
        device = intent.getSerializableExtra("device") as Device
        database = Firebase.database.reference.child("Room").child(device.room).child(device.name)

        tvTimer1 = findViewById<TextView>(R.id.setting_time_tv_turn_on)
        tvTimer2 = findViewById<TextView>(R.id.setting_time_tv_turn_off)
        confirm = findViewById<TextView>(R.id.setting_time_confirm_button)
        stateButton = findViewById<SwitchCompat>(R.id.setting_time_state_switch)

        setUpDatabase()
        createNotificationChannel()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        //set time to turn on/off
        tvTimer1.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                deviceTurnOnTime = sdf.parse(sdf.format(calendar.time))
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_on)
                //changeTurningTimeValue()
                newTvTimer.text = "Set time to power on \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        tvTimer2.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                    timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                deviceTurnOffTime = sdf.parse(sdf.format(calendar.time))
//                test = calendar
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_off)
                newTvTimer.text = "Set time to power off \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        confirm.setOnClickListener {
            setAlarmToTurnOnOffDevice()
        }

        //stateButton.setChecked(true)
        stateButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                displayWhenChangeMode(true)
            }
            else {
                displayWhenChangeMode(false)
                cancelAlarmService()
                Toast.makeText(this,"Set Time Mode Turn Off!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayWhenChangeMode(b: Boolean) {
//        var value = View.INVISIBLE
        var value = View.VISIBLE
//        if(b){value = View.VISIBLE}

        tvTimer1.visibility = value
        tvTimer2.visibility = value
        confirm.visibility = value
        tvTimer1.isEnabled = b
        tvTimer2.isEnabled = b
        confirm.isEnabled = b

        val textColor = Color.parseColor(if (b) "#fda43c" else "#8c8c8c")
        tvTimer2.setTextColor(textColor)
        tvTimer1.setTextColor(textColor)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "ReminderChannel"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =  NotificationChannel("turnonoffnoti", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarmToTurnOnOffDevice() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val now = sdf.parse(sdf.format(Calendar.getInstance().time))

        if (deviceTurnOffTime <= deviceTurnOnTime){
            Toast.makeText(this,"You need to set Turn Off after Turn On time", Toast.LENGTH_SHORT).show()
            return
        }
        if(deviceTurnOffTime.before(now) && deviceTurnOnTime.before(now)) {
            Toast.makeText(this,"Invalid time set", Toast.LENGTH_SHORT).show()
            return
        }

        database.child("ScheduleMode").setValue(true)
        if (deviceTurnOnTime.after(now)){
            createAlarmService(true)
            database.child("OnSchedule").setValue(sdf.format(deviceTurnOnTime))
            Toast.makeText(this,"Set Turn On Time Completed!!!", Toast.LENGTH_SHORT).show()
        }

        if (deviceTurnOffTime.after(now)){
            createAlarmService(false)
            database.child("OffSchedule").setValue(sdf.format(deviceTurnOffTime))
            Toast.makeText(this,"Set Turn Off Time Completed!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAlarmService(b: Boolean) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //create intent
        val intent1 = Intent(this, AlarmReceiver::class.java)
        Log.d("ALARM", device.name)
        val bundle = Bundle()
        bundle.putSerializable("device", device)
        intent1.putExtra("extra", bundle)
        intent1.action = if (b) "TURN_ON_DEVICE" else "TURN_OFF_DEVICE"

        // Alarm time
        val alarmTime = if(b) deviceTurnOnTime.time else deviceTurnOffTime.time

        Log.d("AlarmTurnOnTime", deviceTurnOnTime.toString())
        //pending intent is distinguished by request code
        //request code is timeinmillis + device's name length + device room's name length
        //for case that 2 alarm is set with same time but just 1 device is turned on/off
        val requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent1, 0)

        // require call api version >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //setExactAndAllowWhileIdle: set alarm at exact time and allowed to run in low-power mode
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            val remainTime = (alarmTime - System.currentTimeMillis()) / 1000
            Toast.makeText(this, "created alarm in $remainTime seconds!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAlarmService() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //create intent
        val intent1 = Intent(this, AlarmReceiver::class.java)
        intent1.putExtra("device", device)

        //remove alarm turn on device
        intent1.action = "TURN_ON_DEVICE"
        // Alarm time
        var alarmTime = deviceTurnOnTime.time

        //pending intent is distinguished by request code
        //request code is timeinmillis + device's name length + device room's name length
        //for case that 2 alarm is set with same time but just 1 device is turned on/off
        var requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        var pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent1, 0)
        alarmManager.cancel(pendingIntent)

        //remove alarm turn on device
        intent1.action = "TURN_OFF_DEVICE"
        // Alarm time
        alarmTime = deviceTurnOffTime.time

        //pending intent is distinguished by request code
        //request code is timeinmillis + device's name length + device room's name length
        //for case that 2 alarm is set with same time but just 1 device is turned on/off
        requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent1, 0)
        alarmManager.cancel(pendingIntent)

        //set default value on db
        database.child("ScheduleMode").setValue(false)
    }

    private fun setUpDatabase() {
        //this function is to get data from db and display them on screen

        //get data from realtime database
        val getData = object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                //get value from firebase
                val deviceTimeOffValue = snapshot.child("OffSchedule").value
                val deviceTimeOnValue = snapshot.child("OnSchedule").value
                val deviceScheduleMode = snapshot.child("ScheduleMode").value as Boolean

                //change datetime string to datetime type to display
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val now = sdf.parse(sdf.format(Calendar.getInstance().time))

                stateButton.isChecked = deviceScheduleMode
                displayWhenChangeMode(stateButton.isChecked)
                deviceTurnOnTime = sdf.parse(deviceTimeOnValue.toString())
                deviceTurnOffTime = sdf.parse(deviceTimeOffValue.toString())
//                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
//                val deviceTurnOnTime = LocalDate.parse(deviceTimeOnValue.toString(), formatter)
//                val deviceTurnOffTime = LocalDate.parse(deviceTimeOffValue.toString(), formatter)
                tvTimer1.text = "Set time to power on \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(deviceTurnOnTime)
                tvTimer2.text = "Set time to power off \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(deviceTurnOffTime)

            }
            override fun onCancelled(error: DatabaseError) {
                //no need to implement
            }
        }
        //set listener to getData
        database.addValueEventListener(getData)
        database.addListenerForSingleValueEvent(getData)
    }

}