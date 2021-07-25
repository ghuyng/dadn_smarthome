package com.example.smarthome

import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject

class SetAutoModeActivity : AppCompatActivity() {
    private var lightValue = 0
    private var database = Firebase.database.reference
    private var device = intent.getSerializableExtra("device") as Device
    private var apiController = APIController(this)

    private val confirm = findViewById<TextView>(R.id.setting_auto_confirm)
    private val setDefault = findViewById<TextView>(R.id.setting_auto_set_default)
    private val turnOnSeekBar = findViewById<SeekBar>(R.id.setting_auto_seek_bar_1)
    private val turnOffSeekBar = findViewById<SeekBar>(R.id.setting_auto_seek_bar_2)
    private val stateButton = findViewById<SwitchCompat>(R.id.setting_auto_state_switch)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_auto_mode)
        findViewById<ImageButton>(R.id.setting_auto_back_button).setOnClickListener { onBackPressed()}
        setUpDatabase()

        setDefault.setOnClickListener {
            Toast.makeText(this, "Set Default!!!", Toast.LENGTH_SHORT).show()
            changeDeviceAutoMode(false)
        }
        confirm.setOnClickListener {
            changeDeviceSensorValue()
            Toast.makeText(this,"" + lightValue + " Confirm!!!", Toast.LENGTH_SHORT).show()
        }

        //display sensor value when sliding
        turnOnSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                lightValue = seek.progress
                Toast.makeText(this@SetAutoModeActivity,"Sensor level: " + lightValue, Toast.LENGTH_SHORT).show()
            }
        })

        //set on/off automode
        stateButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                changeDeviceAutoMode(true)
                Toast.makeText(this,"Turn On", Toast.LENGTH_SHORT).show()
            }
            else {
                changeDeviceAutoMode(false)
                Toast.makeText(this,"Turn Off", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //change device's auto mode state on firebase
    private fun changeDeviceAutoMode(b: Boolean) {
        if (b) {
            changeDeviceSensorValue()
        }
        else {
            turnOnSeekBar.progress = 0
            database.child("Room").child(device.room).child(device.name).child("Limit").setValue(0)
        }
    }

    //send message
    private fun changeDeviceSensorValue() {
        //set limit value to db
        var value = turnOnSeekBar.progress
        database.child("Room").child(device.room).child(device.name).child("Limit").setValue(value)

        //send message
        apiController.jsonObjectGET("/"){ res ->
            println(res.toString())
        }

        apiController.jsonObjectPOST("/turn-device", makeMessage(device, value.toString())){ res ->
            println(res.toString())
        }
    }

    //get data from database
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpDatabase() {

        //get data from realtime database
        var getData = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val roomName = device.room
                val deviceName = device.name
                var deviceSenSorValue = snapshot.child("Room").child(roomName).child(deviceName).child("Limit").value

                //check if automode on device is on or off
                var autoModeState = "Off"
                if(deviceSenSorValue.toString().toInt() == -1){
                    autoModeState = "On"
                    deviceSenSorValue = 0
                }

                stateButton.isChecked = autoModeState == "On"
                turnOnSeekBar.setProgress(deviceSenSorValue.toString().toInt(), true)
                //turnOnSeekBar.setProgress(100,true)
                for(i in snapshot.children){ }
            }
            override fun onCancelled(error: DatabaseError) {
                //no need to implement
            }
        }
        //set listener to getData
        database.addValueEventListener(getData)
        database.addListenerForSingleValueEvent(getData)
    }

    private fun makeMessage(device: Device, data: String): JSONObject {
        val message = JSONObject()
        message.put("id", "13")
        message.put("name", "LIGHT")
        message.put("data", data)
        message.put("unit", "")
        return message
    }

}