package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewDeviceActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var mqttService: MQTTService
    private var utility = Utility(this)
    private lateinit var device: Device
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mDatabase = Firebase.database.reference
        device = intent.getSerializableExtra("device") as Device
        mDatabase.child("Room/${device.room}/${device.name}").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FIREBASE", snapshot.child("Status").value.toString())
                device.status = snapshot.child("Status").value as Boolean
                findViewById<TextView>(R.id.device_status).text = getDeviceStatus(device)

                if (device.deviceType == DeviceType.Light) {
                    val deviceTurnOffValue = snapshot.child("TurnOffValue").value
                    val deviceTurnOnValue = snapshot.child("TurnOnValue").value
                    var autoModeState = "ON"
                    if (deviceTurnOnValue.toString().toInt() == -1) {
                        autoModeState = "OFF"
                    }
                    if (deviceTurnOffValue.toString().toInt() == -1) {
                        autoModeState = "OFF"
                    }
                    findViewById<TextView>(R.id.device_auto_mode).text = autoModeState
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "loadPost:onCancelled", error.toException())
            }

        })
        setContentView(R.layout.activity_view_device)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { onBackPressed() }
        findViewById<ImageButton>(R.id.power_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.power_icon_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.setting_button).setOnClickListener{ showDeviceSettingPopup() }

        findViewById<TextView>(R.id.device_room).text = device.room
        findViewById<TextView>(R.id.device_name).text = device.name
        findViewById<ImageView>(R.id.device_image).setImageDrawable(utility.getDeviceImage(device))

        if (device.deviceType != DeviceType.Light) {
            findViewById<TextView>(R.id.automode_text).visibility = View.GONE
            findViewById<MaterialTextView>(R.id.device_auto_mode).visibility = View.GONE
        }
        if (device.deviceType == DeviceType.Door) {
            findViewById<ImageButton>(R.id.setting_button).visibility = View.GONE
        }
    }

    private fun changeDeviceStatus(){
        val device = intent.getSerializableExtra("device") as Device
        utility.turnOnOffDevice(device)
        findViewById<TextView>(R.id.device_status).text = getDeviceStatus(intent.getSerializableExtra("device") as Device)


    }

    private fun getDeviceStatus(device: Device): String {
        if (device.deviceType == DeviceType.Door) {
            return if (device.status) "LOCKED" else "UNLOCKED"
        }
        return if (device.status) "ON" else "OFF"
    }

    private fun showDeviceSettingPopup(){
        val button = findViewById<ImageButton>(R.id.setting_button)
        val popup = PopupMenu(this, button)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.setting_devide_popup_menu)
        if (device.deviceType != DeviceType.Light) {
            popup.menu.getItem(1).isVisible = false
        }
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.option_set_power_schedule -> {
                val i = Intent(this, SetTurnOnOffTimeActivity::class.java)
                val device = intent.getSerializableExtra("device") as Device
                i.putExtra("device", device)
                startActivity(i)
//                Toast.makeText(this, ,"Set power selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.option_set_auto_mode -> {
                val i = Intent(this, SetAutoModeActivity::class.java)
                val device = intent.getSerializableExtra("device") as Device
                i.putExtra("device", device)
                startActivity(i)
                true
            }
            else -> false
        }
    }

}