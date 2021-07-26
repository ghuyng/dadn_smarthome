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
import io.socket.client.Socket
import org.json.JSONObject

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
            mDatabase.child("Room").addValueEventListener(object: ValueEventListener{
                override fun onDataChange(it: DataSnapshot) {
                    roomList = mutableListOf()
                    (it.value as Map<String, *>).forEach { roomData ->
                        val room = Room(roomData.key)
                        room.deviceList = mutableListOf()
                        (roomData.value as Map<String, *>).forEach { deviceData ->
                            room.deviceList.add(parseDeviceFromDB(room.name, deviceData))
                        }
                        roomList.add(room)
                    }

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
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



//            mDatabase.child("Room").get().addOnSuccessListener {
//                roomList = mutableListOf()
//                (it.value as Map<String, *>).forEach { roomData ->
//                    val room = Room(roomData.key)
//                    room.deviceList = mutableListOf()
//                    (roomData.value as Map<String, *>).forEach { deviceData ->
//                        room.deviceList.add(parseDeviceFromDB(room.name, deviceData))
//                    }
//                    roomList.add(room)
//                }
//
//                binding = ActivityMainBinding.inflate(layoutInflater)
//                setContentView(binding.root)
//                val navView: BottomNavigationView = binding.navView
//
//                val navController = findNavController(R.id.nav_host_fragment_activity_main)
//                // Passing each menu ID as a set of Ids because each
//                // menu should be considered as top level destinations.
//                val appBarConfiguration = AppBarConfiguration(
//                    setOf(
//                        R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//                    )
//                )
//                setupActionBarWithNavController(navController, appBarConfiguration)
//                navView.setupWithNavController(navController)
//
//            }.addOnFailureListener {
//                Log.w("FIREBASE", "loadPost:onCancelled", it)
//            }

            Toast.makeText(this, "Welcome ${currentUser.email}", Toast.LENGTH_SHORT).show()

            if (intent.extras != null) {
                for (key in intent.extras!!.keySet()) {
                    val value = intent.extras!![key]
                    Log.d("NOTI MAIN", "Key: $key Value: $value")
                }
            }

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