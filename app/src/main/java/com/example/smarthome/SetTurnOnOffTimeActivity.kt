package com.example.smarthome

import android.app.AlertDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class SetTurnOnOffTimeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_turn_on_off_time)

        val tvTimer1 = findViewById<TextView>(R.id.setting_time_tv_turn_on)
        val tvTimer2 = findViewById<TextView>(R.id.setting_time_tv_turn_off)
        val confirm  = findViewById<TextView>(R.id.setting_time_confirm_button)
        findViewById<ImageButton>(R.id.setting_time_back_button).setOnClickListener { onBackPressed() }
        findViewById<TextView>(R.id.setting_time_device_status).text = "ON"


        //set time to turn on/off
        tvTimer1.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_on)
                newTvTimer.text = "Set time to power on \n" + SimpleDateFormat("HH: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        tvTimer2.setOnClickListener {
            val calendar = Calendar.getInstance()
            val timesetListener = TimePickerDialog.OnTimeSetListener{
                    timePicker, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                val newTvTimer = findViewById<TextView>(R.id.setting_time_tv_turn_off)
                newTvTimer.text = "Set time to power off \n" + SimpleDateFormat("HH: mm aa").format(calendar.time)
            }
            TimePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog, timesetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }

        confirm.setOnClickListener {
            Toast.makeText(this,"Confirm!!!", Toast.LENGTH_SHORT).show()
        }

    }
}