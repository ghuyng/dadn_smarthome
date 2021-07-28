package com.example.smarthome

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SetAutoModeActivity : AppCompatActivity() {
    private lateinit var device : Device
    private var lightValue = 0
    private lateinit var database : DatabaseReference

    private lateinit var confirm : TextView
    private lateinit var setDefault : TextView
    private lateinit var stateButton : SwitchCompat
    private lateinit var blankText : TextView

    private lateinit var turnOnText : TextView
    private lateinit var turnOnLowerBound : TextView
    private lateinit var turnOnUpperBound : TextView
    private lateinit var turnOffText : TextView
    private lateinit var turnOffLowerBound : TextView
    private lateinit var turnOffUpperBound : TextView
    private lateinit var turnOnSeekBar : SeekBar
    private lateinit var turnOffSeekBar: SeekBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_auto_mode)
        findViewById<ImageButton>(R.id.setting_auto_back_button).setOnClickListener { onBackPressed()}
        device = intent.getSerializableExtra("device") as Device
        database = Firebase.database.reference.child("Room").child(device.room).child(device.name)
        setUpDatabase()

        confirm = findViewById<TextView>(R.id.setting_auto_confirm)
        setDefault = findViewById<TextView>(R.id.setting_auto_set_default)
        stateButton = findViewById<SwitchCompat>(R.id.setting_auto_state_switch)

        blankText = findViewById(R.id.setting_auto_blank_text)
        turnOnText = findViewById(R.id.setting_auto_turnon_text)
        turnOnLowerBound = findViewById<TextView>(R.id.setting_auto_lowerbound_1)
        turnOnUpperBound = findViewById<TextView>(R.id.setting_auto_upperbound_1)
        turnOffText = findViewById<TextView>(R.id.setting_auto_turnoff_text)
        turnOffLowerBound = findViewById<TextView>(R.id.setting_auto_lowerbound_2)
        turnOffUpperBound = findViewById<TextView>(R.id.setting_auto_upperbound_2)
        turnOnSeekBar = findViewById<SeekBar>(R.id.setting_auto_seek_bar_1)
        turnOffSeekBar = findViewById<SeekBar>(R.id.setting_auto_seek_bar_2)

        setDefault.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                turnOnSeekBar.setProgress(100, true)
                turnOffSeekBar.setProgress(700, true)
            }
            Toast.makeText(this, "Set Default turn on value is 100, turn off value is 700!!!", Toast.LENGTH_SHORT).show()
        }
        confirm.setOnClickListener {
            if (changeDeviceSensorValue(stateButton.isChecked)) {
                Toast.makeText(this,"Auto mode with turn on: " + turnOnSeekBar.progress + ", turn off: " + turnOffSeekBar.progress + ".Confirm!!!", Toast.LENGTH_SHORT).show()
            }
        }

        setSeekBarTracking()

        //set on/off automode
        stateButton.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                displayWhenChangeMode(true)
            }
            else {
                displayWhenChangeMode(false)
                changeDeviceSensorValue(false)
                Toast.makeText(this,"Auto mode turned off!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setSeekBarTracking() {
        //display sensor value when sliding
        turnOnSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                turnOnUpperBound.text = progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                lightValue = seek.progress
            }
        })

        //display sensor value when sliding
        turnOffSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                turnOffUpperBound.text = progress.toString()
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                lightValue = seek.progress
            }
        })
    }

    private fun displayWhenChangeMode(b: Boolean) {
        var value = View.VISIBLE
        if(!b) {value = View.INVISIBLE}

        confirm.visibility = value
        setDefault.visibility = value
        blankText.visibility = value

        turnOnText.visibility = value
        turnOnLowerBound.visibility = value
        turnOnUpperBound.visibility = value
        turnOffText.visibility = value
        turnOffLowerBound.visibility = value
        turnOffUpperBound.visibility = value
        turnOnSeekBar.visibility = value
        turnOffSeekBar.visibility = value
    }


    //send message
    private fun changeDeviceSensorValue(b: Boolean = true) : Boolean{
        var turnOnVal = turnOnSeekBar.progress
        var turnOffVal = turnOffSeekBar.progress

        if (!b){
            turnOnVal = -1
            turnOffVal = -1
        }

        //check and set limit value to db
        if(turnOnVal > turnOffVal){
            Toast.makeText(this, "You should set Turn Off Value larger than Turn On Value", Toast.LENGTH_SHORT).show()
            return false
        }

        val childUpdates = hashMapOf<String, Any>(
            "/TurnOnValue" to turnOnVal,
            "/TurnOffValue" to turnOffVal
        )
        database.updateChildren(childUpdates)

        return true
    }

    //get data from database
    private fun setUpDatabase() {

        //get data from realtime database
        var getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var deviceTurnOnValue = snapshot.child("TurnOnValue").value
                var deviceTurnOffValue = snapshot.child("TurnOffValue").value
                //check if automode on device is on or off
                var autoModeState = "On"
                if (deviceTurnOnValue.toString().toInt() == -1){
                    autoModeState = "Off"
                    deviceTurnOnValue = 0
                }
                if (deviceTurnOffValue.toString().toInt() == -1){
                    autoModeState = "Off"
                    deviceTurnOffValue = 0
                }

                stateButton.isChecked = autoModeState == "On"
                displayWhenChangeMode(stateButton.isChecked)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    turnOnSeekBar.setProgress(deviceTurnOnValue.toString().toInt(), true)
                    turnOffSeekBar.setProgress(deviceTurnOffValue.toString().toInt(), true)
                }
                //turnOnSeekBar.setProgress(100,true)
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