package com.example.smarthome

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smarthome.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var mqttService: MQTTService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mqttService = MQTTService(this)
        mqttService.setCallback(object: MqttCallbackExtended{
            override fun connectionLost(cause: Throwable?) {

            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val data_to_microbit = message.toString()
//                port.write(data_to_microbit.toByteArray(), 1000)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {

            }

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {

            }

        })
    }

    private fun sendDataMQTT(data: String){
        val msg = MqttMessage()
        msg.id = 1234
        msg.qos = 0
        msg.isRetained = true

        val b = data.toByteArray(Charset.forName("UTF-8"))
        msg.payload = b

        Log.d("ABC", "Publish:" + msg)
        try{
            mqttService.mqttAndroidClient.publish("[Your subscription topic]", msg)
        }catch (e: MqttException){
            Log.d("MQTT", "sendDataMQTT: cannot send message")
        }
    }
}