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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSocket: Socket
    private var mDatabase: DatabaseReference = Firebase.database.reference

    companion object {
        @SuppressLint("StaticFieldLeak")
        var roomList = mutableListOf<Room>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            val intent = Intent(this, SignInActivity::class.java)
//            startActivityForResult(intent, 123)
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(this, "Welcome ${currentUser.email}", Toast.LENGTH_SHORT).show()
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
            } catch (e: URISyntaxException) {
            }
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
                apiController.jsonObjectPOST(
                    "/set-registrationtoken",
                    JSONObject().put("message", token)
                ) { res ->
                    Log.d("MainActivity", res.toString())
                }
            })


            val roomListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    roomList = mutableListOf()
                    (dataSnapshot.value as Map<String, *>).forEach { roomData ->
                        val room = Room(roomData.key)
                        room.deviceList = mutableListOf()
                        (roomData.value as Map<String, *>).forEach { deviceData ->
                            room.deviceList.add(parseDeviceFromDB(room.name, deviceData))
                        }
                        roomList.add(room)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("FIREBASE", "loadPost:onCancelled", databaseError.toException())
                }
            }
            mDatabase.child("Room").addValueEventListener(roomListener)
        }

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

    private fun parseDeviceFromDB(roomName: String, data: Map.Entry<String, *>): Device {
        val name = data.key
        val deviceAttrs = data.value as Map<String, *>
        val type = DeviceType.valueOf(deviceAttrs["Type"] as String)
        val status = deviceAttrs["Status"] as Boolean

        return Device(name, roomName, type, status)
    }

}