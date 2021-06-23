package com.example.smarthome

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smarthome.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mqttService: MQTTService
        var roomList: List<Room> = listOf(
            Room("Living Room"), Room("Kitchen"),
            Room("BedRoom"), Room("Bath Room"), Room("Garage")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val intent = Intent(this, SignInActivity::class.java)
        startActivityForResult(intent, 123)

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

        try {
            mSocket = IO.socket("http://192.168.1.6:3000")
            mSocket.connect()
            mSocket.emit("switchRelay", "abcxyz")
        } catch (e: URISyntaxException) {}
        mSocket.on("alert", Emitter.Listener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, 200)
        })

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            var apiController = APIController(this)
            apiController.jsonObjectPOST("/set-registrationtoken", JSONObject().put("message", token)){
                    res -> Log.d("MainActivity", res.toString())
            }
        })
        mqttService = MQTTService(this.applicationContext)
        mqttService.setCallback(object : MqttCallbackExtended {
            override fun connectionLost(cause: Throwable?) {}

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val data_to_microbit = message.toString()
//                port.write(data_to_microbit.toByteArray(), 1000)
                Toast.makeText(applicationContext, data_to_microbit, Toast.LENGTH_SHORT).show()
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {}

            override fun connectComplete(reconnect: Boolean, serverURI: String?) {}

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            if(resultCode == Activity.RESULT_OK){
                val result = data?.getStringExtra("UserEmail");
                Toast.makeText(this, "Welcome ${result.toString()}", Toast.LENGTH_SHORT).show()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }
    }

}