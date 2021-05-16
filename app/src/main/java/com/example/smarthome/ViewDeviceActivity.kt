package com.example.smarthome

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class ViewDeviceActivity : AppCompatActivity() {

    private lateinit var mqttService: MQTTService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_device)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener { onBackPressed() }
        findViewById<ImageButton>(R.id.power_button).setOnClickListener { changeDeviceStatus()}
        findViewById<ImageButton>(R.id.power_icon_button).setOnClickListener { changeDeviceStatus()}

        val device = intent.getSerializableExtra("device") as Device
        findViewById<TextView>(R.id.device_room).text = device.room
        findViewById<TextView>(R.id.device_name).text = device.name
        findViewById<TextView>(R.id.device_status).text = if (device.status) "ON" else "OFF"

        mqttService = MQTTService(this.applicationContext)
        mqttService.setCallback(object: MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val data_to_microbit = message.toString()
                Toast.makeText(applicationContext, data_to_microbit, Toast.LENGTH_SHORT).show()
//                port.write(data_to_microbit.toByteArray(), 1000)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {

            }

        })
    }

    private fun changeDeviceStatus(){
        val device = intent.getSerializableExtra("device") as Device
        device.status = !device.status
        val statusStr: String = if (device.status) "ON" else "OFF"
        findViewById<TextView>(R.id.device_status).text = statusStr
        mqttService.sendDataMQTT("hello")
    }
}