package com.example.smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.smarthome.model.RoomReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class DevicereportActivity : AppCompatActivity() {
    val list = ArrayList<RoomReport>()
    lateinit var adapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devicereport)
        adapter = MyAdapter(this, R.layout.row, list)
        val listView = findViewById<ListView>(R.id.lvRoom)
        listView.adapter = adapter

        //listView.adapter = MyAdapter(this,R.layout.row,list)
        //Back button
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
        addControls()
    }

    private fun addControls() {
        val deviceName: TextView = findViewById(R.id.device_name) as TextView
        val description: TextView = findViewById(R.id.most_used) as TextView
        val deviceImage: ImageView = findViewById(R.id.device_image) as ImageView
        val intent = getIntent()
        val device:String = intent.getStringExtra("value").toString()
        deviceName.text =device
        description.setText("Total active time of " +device + " "+"per room." )

        //If button Fan was pressed
        if (device == "Fan") {
            val toast = Toast.makeText(this, "View fans consumption",Toast.LENGTH_SHORT)
            toast.show()
            description.setText("Total active time of fans per room." )

            deviceImage.setImageResource(R.drawable.img_fan)
            roomConsumption(device,"Living Room")
            roomConsumption(device,"Kitchen")
            roomConsumption(device,"Bedroom 1")
            roomConsumption(device,"Bedroom 2")
            roomConsumption(device,"Garage")

        }

        //If button Light was pressed
        if (device == "Light") {
            val toast = Toast.makeText(this, "View lights consumption",Toast.LENGTH_SHORT)
            toast.show()
            description.setText("Total active time of lights per room." )

            deviceImage.setImageResource(R.drawable.img_light)
            roomConsumption(device,"Living Room")
            roomConsumption(device,"Kitchen")
            roomConsumption(device,"Bedroom 1")
            roomConsumption(device,"Bedroom 2")
            roomConsumption(device,"Garage")
        }
        //If AC was pressed
        if (device == "Air-Conditioners") {
            val toast = Toast.makeText(this, "View AC consumption",Toast.LENGTH_SHORT)
            toast.show()
            description.setText("Total active time of air-conditioners per room." )

            deviceImage.setImageResource(R.drawable.img_airconditioner)
            roomConsumption(device,"Living Room")
            roomConsumption(device,"Kitchen")
            roomConsumption(device,"Bedroom 1")
            roomConsumption(device,"Bedroom 2")
            roomConsumption(device,"Garage")
        }
        //If TV was pressed
        if (device == "Television") {
            val toast = Toast.makeText(this, "View TV consumption",Toast.LENGTH_SHORT)
            toast.show()
            description.setText("Total active time of televisions per room." )

            deviceImage.setImageResource(R.drawable.img_tv)
            roomConsumption(device,"Living Room")
            roomConsumption(device,"Kitchen")
            roomConsumption(device,"Bedroom 1")
            roomConsumption(device,"Bedroom 2")
            roomConsumption(device,"Garage")
        }

    }

    private fun roomConsumption(device: String, room: String) {
        var roomTime = "0"
        val database: DatabaseReference = Firebase.database.reference
        val myRef = database.child("Room").child(room)
        val timeListener= object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var allDevice:Long = 0
                for (i in dataSnapshot.children){ //i là devices
                    if (i.child("Type").getValue().toString() ==device) {
                        var timePerDevice: Long = 0
                        for (j in 0..i.child("Off").childrenCount - 1) {
                            val dateOffStr: String =
                                i.child("Off").child(j.toString()).getValue().toString()
                            val dateOnStr: String =
                                i.child("On").child(j.toString()).getValue().toString()
                            //Thoi gian moi lan bat tat
                            val datePair: Long = subtractDate(dateOffStr,dateOnStr)
                            //Tong thoi gian 1 thiet bi
                            timePerDevice += datePair
                        }
                        allDevice+=timePerDevice
                    }
                }
                var roomTimeinMinute:Double =  allDevice*0.0001/6.0
                roomTime= roomTimeinMinute.toInt().toString()
                //Log.d("roomDetail", room+roomTime)
                list.add(RoomReport(room,roomTime + " "+"Minutes"))
                adapter.notifyDataSetChanged()

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("Fan", databaseError.message)
            }
        }
        myRef.addListenerForSingleValueEvent(timeListener)
    }


    private fun subtractDate(dateOffStr: String, dateOnStr: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val dateOff: java.util.Date = sdf.parse(dateOffStr)
        val dateOn: java.util.Date = sdf.parse(dateOnStr)

        val datePair: Long = dateOff.getTime() - dateOn.getTime()
        return datePair
    }


}