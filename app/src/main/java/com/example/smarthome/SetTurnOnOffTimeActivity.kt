package com.example.smarthome

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
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

    private lateinit var tvTimerOn : TextView
    private lateinit var tvTimerOff : TextView
    private lateinit var confirm  : TextView
    private lateinit var stateButtonOn : SwitchCompat
    private lateinit var stateButtonOff : SwitchCompat

    private lateinit var device :Device
    private lateinit var deviceTurnOnTime : Date
    private lateinit var deviceTurnOffTime : Date
    private var deviceScheduleMode: Int = 0

    enum class ScheduleMode {
        BOTH_OFF,
        TURN_ON_DEVICE,
        TURN_OFF_DEVICE,
        BOTH_ON
    }
    @SuppressLint("CutPasteId", "SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_turn_on_off_time)
        findViewById<ImageButton>(R.id.setting_time_back_button).setOnClickListener { onBackPressed() }
        device = intent.getSerializableExtra("device") as Device
        database = Firebase.database.reference.child("Room").child(device.room).child(device.name)

        tvTimerOn = findViewById<TextView>(R.id.setting_time_tv_turn_on)
        tvTimerOff = findViewById<TextView>(R.id.setting_time_tv_turn_off)
        confirm = findViewById<TextView>(R.id.setting_time_confirm_button)
        stateButtonOn = findViewById<SwitchCompat>(R.id.setting_time_on_state_switch)
        stateButtonOff = findViewById<SwitchCompat>(R.id.setting_time_off_state_switch)

        setUpDatabase()
        createNotificationChannel()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        //set time to turn on/off
        tvTimerOn.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                val now = Calendar.getInstance()
                if (calendar.before(now)) {
                    calendar.add(Calendar.DATE, 1)
                }
                deviceTurnOnTime = sdf.parse(sdf.format(calendar.time))
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_on)
                //changeTurningTimeValue()
                newTvTimer.text = "Set time to power on \n" + SimpleDateFormat("hh: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        tvTimerOff.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                    timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                val now = Calendar.getInstance()
                if (calendar.before(now)) {
                    calendar.add(Calendar.DATE, 1)
                }
                deviceTurnOffTime = sdf.parse(sdf.format(calendar.time))
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_off)
                newTvTimer.text = "Set time to power off \n" + SimpleDateFormat("hh: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        confirm.setOnClickListener {
            setAlarmToTurnOnOffDevice()
        }

        stateButtonOn.setOnCheckedChangeListener { buttonView, isChecked ->
            val textColor = Color.parseColor(if (isChecked) "#fda43c" else "#8c8c8c")
            tvTimerOn.setTextColor(textColor)
            deviceScheduleMode = deviceScheduleMode or ScheduleMode.TURN_ON_DEVICE.ordinal
            if (!isChecked){
                deviceScheduleMode = deviceScheduleMode and ScheduleMode.TURN_OFF_DEVICE.ordinal
                cancelAlarmService(true)
            }
        }

        stateButtonOff.setOnCheckedChangeListener { buttonView, isChecked ->
            val textColor = Color.parseColor(if (isChecked) "#fda43c" else "#8c8c8c")
            tvTimerOff.setTextColor(textColor)
            deviceScheduleMode = deviceScheduleMode or ScheduleMode.TURN_OFF_DEVICE.ordinal
            if (!isChecked){
                deviceScheduleMode = deviceScheduleMode and ScheduleMode.TURN_ON_DEVICE.ordinal
                cancelAlarmService(false)
            }
        }
    }

    private fun displayWhenChangeMode(scheduleMode: Int) {
        val enableTextColor = Color.parseColor("#fda43c")
        val disableTextColor = Color.parseColor("#8c8c8c")
        if ((scheduleMode and ScheduleMode.TURN_ON_DEVICE.ordinal) > 0){
            tvTimerOn.setTextColor(enableTextColor)
        } else {
            tvTimerOn.setTextColor(disableTextColor)
        }

        if ((scheduleMode and ScheduleMode.TURN_OFF_DEVICE.ordinal) > 0) {
            tvTimerOff.setTextColor(enableTextColor)
        } else {

            tvTimerOff.setTextColor(disableTextColor)
        }
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

        database.child("ScheduleMode").setValue(deviceScheduleMode)
        if (deviceTurnOnTime.after(now) && stateButtonOn.isChecked){
            createAlarmService(true)
            database.child("OnSchedule").setValue(sdf.format(deviceTurnOnTime))
        }

        if (deviceTurnOffTime.after(now) && stateButtonOff.isChecked){
            createAlarmService(false)
            database.child("OffSchedule").setValue(sdf.format(deviceTurnOffTime))
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
            val hours = remainTime / 3600
            val minutes = remainTime / 60 - hours * 60
            val hourText = if (hours > 0) "$hours hour(s)" else ""
            val minuteText = if (minutes > 0) "$minutes minute(s)" else ""
            val remainTimeText = if (hours > 0 || minutes > 0) "$hourText $minuteText" else "less than 1 minute"
            Toast.makeText(this, "Created alarm in $remainTimeText", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAlarmService(b: Boolean) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //create intent
        val intent1 = Intent(this, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("device", device)
        intent1.putExtra("extra", bundle)
        var alarmTime: Long
        var requestCode: Int
        if (b) {

            //remove alarm turn on device
            intent1.action = "TURN_ON_DEVICE"
            // Alarm time
            alarmTime = deviceTurnOnTime.time

            //pending intent is distinguished by request code
            //request code is timeinmillis + device's name length + device room's name length
            //for case that 2 alarm is set with same time but just 1 device is turned on/off
            requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        }
        else{
            //remove alarm turn on device
            intent1.action = "TURN_OFF_DEVICE"
            // Alarm time
            alarmTime = deviceTurnOffTime.time

            //pending intent is distinguished by request code
            //request code is timeinmillis + device's name length + device room's name length
            //for case that 2 alarm is set with same time but just 1 device is turned on/off
            requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        }
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent1, 0)
        alarmManager.cancel(pendingIntent)
        database.child("ScheduleMode").setValue(deviceScheduleMode)
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
                deviceScheduleMode = snapshot.child("ScheduleMode").value.toString().toInt()

                //change datetime string to datetime type to display
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val now = Calendar.getInstance()
                var calendar = Calendar.getInstance()

                stateButtonOn.isChecked = (deviceScheduleMode and ScheduleMode.TURN_ON_DEVICE.ordinal) > 0
                stateButtonOff.isChecked = (deviceScheduleMode and ScheduleMode.TURN_OFF_DEVICE.ordinal) > 0
                displayWhenChangeMode(deviceScheduleMode)
                deviceTurnOnTime = sdf.parse(deviceTimeOnValue.toString())
                deviceTurnOffTime = sdf.parse(deviceTimeOffValue.toString())
                if (deviceTurnOnTime.before(now.time)) {
                    calendar.time = deviceTurnOnTime
                    calendar.add(Calendar.DATE, 1)
                    deviceTurnOnTime = calendar.time
                }
                if (deviceTurnOffTime.before(now.time)) {
                    calendar.time = deviceTurnOffTime
                    calendar.add(Calendar.DATE, 1)
                    deviceTurnOffTime = calendar.time
                }
                tvTimerOn.text = "Set time to power on \n" + SimpleDateFormat("hh: mm aa").format(deviceTurnOnTime)
                tvTimerOff.text = "Set time to power off \n" + SimpleDateFormat("hh: mm aa").format(deviceTurnOffTime)

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