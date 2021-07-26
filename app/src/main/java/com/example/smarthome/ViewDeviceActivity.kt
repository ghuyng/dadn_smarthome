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
import org.json.JSONObject

class ViewDeviceActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var mqttService: MQTTService
    private var utility = Utility(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val device = intent.getSerializableExtra("device") as Device
        val mDatabase = Firebase.database.reference
        mDatabase.child("Room/${device.room}/${device.name}/Status").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FIREBASE", snapshot.value.toString())
                device.status = snapshot.value as Boolean
                findViewById<TextView>(R.id.device_status).text = getDeviceStatus(device)
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

        if (device.deviceType == DeviceType.Door) {
            findViewById<TextView>(R.id.automode_text).visibility = View.GONE
            findViewById<MaterialTextView>(R.id.device_auto_mode).visibility = View.GONE
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

    private fun makeMessage(device: Device, data: String): JSONObject{
        val message = JSONObject()
        message.put("id", "11")
        message.put("name", "RELAY")
        message.put("data", data)
        message.put("unit", "")
        return message
    }


    private fun showDeviceSettingPopup(){
        val button = findViewById<ImageButton>(R.id.setting_button)
        val popup = PopupMenu(this, button)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.setting_devide_popup_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.option_set_power_schedule -> {
                val i = Intent(this, SetTurnOnOffTimeActivity::class.java)
                val device = intent.getSerializableExtra("device") as Device
                i.putExtra("device", device)
                finish()
                startActivity(i)
//                Toast.makeText(this, ,"Set power selected", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.option_set_auto_mode -> {
                val i = Intent(this, SetAutoModeActivity::class.java)
                val device = intent.getSerializableExtra("device") as Device
                i.putExtra("device", device)
                startActivity(i)
                finish()
                true
            }
            else -> false
        }
    }

}