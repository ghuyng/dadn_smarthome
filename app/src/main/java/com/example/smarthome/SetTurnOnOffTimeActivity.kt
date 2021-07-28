package com.example.smarthome

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class SetTurnOnOffTimeActivity : AppCompatActivity() {

    private var database = Firebase.database.reference
    private var apiController = APIController(this)

    private lateinit var tvTimer1 : TextView
    private lateinit var tvTimer2 : TextView
    private lateinit var confirm  : TextView
    private lateinit var stateButton : SwitchCompat

    private lateinit var device :Device
    private lateinit var deviceTurnOnTime : Date
    private lateinit var deviceTurnOffTime : Date
    private lateinit var test: Calendar
    @SuppressLint("CutPasteId", "SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_turn_on_off_time)
        findViewById<ImageButton>(R.id.setting_time_back_button).setOnClickListener { onBackPressed() }
        device = intent.getSerializableExtra("device") as Device

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
                deviceTurnOffTime = sdf.parse(sdf.format(calendar.time))
                test = calendar
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
                Toast.makeText(this,"Set Time Mode Turn On!!", Toast.LENGTH_SHORT).show()
            }
            else {
                displayWhenChangeMode(false)
                cancelAlarmService()
                Toast.makeText(this,"Set Time Mode Turn Off!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayWhenChangeMode(b: Boolean) {
        var value = View.INVISIBLE
        if(b){value = View.VISIBLE}

        tvTimer1.visibility = value
        tvTimer2.visibility = value
        confirm.visibility = value
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

        if (deviceTurnOnTime.after(now)){
            createAlarmService(true)
            database.child("Room").child(device.room).child(device.name).child("OnSchedule").setValue(sdf.format(deviceTurnOnTime))
            Toast.makeText(this,"Set Turn On Time Completed!!!", Toast.LENGTH_SHORT).show()
        }

        if (deviceTurnOffTime.after(now)){
            createAlarmService(false)
            database.child("Room").child(device.room).child(device.name).child("OffSchedule").setValue(sdf.format(deviceTurnOffTime))
            Toast.makeText(this,"Set Turn Off Time Completed!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAlarmService(b: Boolean) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //create intent
        val intent1 = Intent(this, AlarmReceiver::class.java)
        val device = intent.getSerializableExtra("device") as Device
        intent1.putExtra("device", device)
        if (b){intent1.action = "TURN_ON_DEVICE"}
        else {intent1.action = "TURN_OFF_DEVICE"}

        // Alarm time
        var alarmTime = deviceTurnOffTime.time
        if (b) {alarmTime = test.timeInMillis}
        val ALARM_DELAY_IN_SECOND = 10
        val alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L

        Log.d("AlarmTurnOnTime", deviceTurnOnTime.toString())
        //pending intent is distinguished by request code
        //request code is timeinmillis + device's name length + device room's name length
        //for case that 2 alarm is set with same time but just 1 device is turned on/off
        val requestCode = alarmTime.hashCode() + device.name.length + device.room.length
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent1, 0)

        // require call api version >= 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //setExactAndAllowWhileIdle: set alarm at exact time and allowed to run in low-power mode
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeAtUTC, pendingIntent)
            Toast.makeText(this, "create alarm completed!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelAlarmService() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //create intent
        val intent1 = Intent(this, AlarmReceiver::class.java)
        val device = intent.getSerializableExtra("device") as Device
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
        val tempTime = "2000-12-31 00:00:01"
        database.child("Room").child(device.room).child(device.name).child("OnSchedule").setValue(tempTime)
        database.child("Room").child(device.room).child(device.name).child("OffSchedule").setValue(tempTime)
    }

    private fun setUpDatabase() {
        //this function is to get data from db and display them on screen
        //get the room and device's name
        val roomName = device.room
        val deviceName = device.name

        //get data from realtime database
        var getData = object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                //get value from firebase
                var deviceTimeOffValue = snapshot.child("Room").child(roomName).child(deviceName).child("OffSchedule").value
                var deviceTimeOnValue = snapshot.child("Room").child(roomName).child(deviceName).child("OnSchedule").value

                //change datetime string to datetime type to display
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val now = sdf.parse(sdf.format(Calendar.getInstance().time))

//                val now = sdf.(Calendar.getInstance().toString())
                deviceTurnOnTime = sdf.parse(deviceTimeOnValue.toString())
                deviceTurnOffTime = sdf.parse(deviceTimeOffValue.toString())
//                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")
//                val deviceTurnOnTime = LocalDate.parse(deviceTimeOnValue.toString(), formatter)
//                val deviceTurnOffTime = LocalDate.parse(deviceTimeOffValue.toString(), formatter)
                tvTimer1.text = "Set time to power on \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(deviceTurnOnTime)
                tvTimer2.text = "Set time to power off \n" + SimpleDateFormat("yyyy/MM/dd HH: mm aa").format(deviceTurnOffTime)

                // check turnonoff mode state based on time
                // turnon ---- now --- turnoff or now --- turnon --- turnoff -> true
                //else false
                if (deviceTurnOnTime.before(now) && deviceTurnOffTime.before(now)){
                    stateButton.isChecked = false
                }
                else {
                    stateButton.isChecked = true
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //no need to implement
            }
        }
        //set listener to getData
        database.addValueEventListener(getData)
        database.addListenerForSingleValueEvent(getData)
    }


    private fun changeDeviceTurningState(b: Boolean){
        if(b){
            setAlarmToTurnOnOffDevice()
        }
        else{
            val tempTime = "2000-12-31 00:00:01"
            database.child("Room").child(device.room).child(device.name).child("OnSchedule").setValue(tempTime)
            database.child("Room").child(device.room).child(device.name).child("OffSchedule").setValue(tempTime)
        }
    }

}