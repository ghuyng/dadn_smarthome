package com.example.smarthome

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class ViewDeviceActivity : AppCompatActivity() {

    private lateinit var mqttService: MQTTService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_device)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { onBackPressed() }
        findViewById<ImageButton>(R.id.power_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.power_icon_button).setOnClickListener { changeDeviceStatus()}

        val device = intent.getSerializableExtra("device") as Device
        mqttService = MainActivity.mqttService
        findViewById<TextView>(R.id.device_room).text = device.room
        findViewById<TextView>(R.id.device_name).text = device.name
        findViewById<TextView>(R.id.device_status).text = if (device.status) "ON" else "OFF"
        findViewById<ImageView>(R.id.device_image).setImageDrawable(Ultility(this).getDeviceImage(device))
    }

    private fun changeDeviceStatus(){
        val device = intent.getSerializableExtra("device") as Device
        device.status = !device.status
        val statusStr: String = if (device.status) "ON" else "OFF"
        findViewById<TextView>(R.id.device_status).text = statusStr
        mqttService.sendDataMQTT(makeMessage(device, (if (device.status) 1 else 0).toString()).toString())
    }

    private fun makeMessage(device: Device, data: String): JSONObject{
        val message = JSONObject()
        message.put("id", "11")
        message.put("data", data)
        message.put("unit", "")
        message.put("name", "RELAY")
        return message
    }
}