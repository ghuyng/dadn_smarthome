package com.example.smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.smarthome.model.RoomReport

class DevicereportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devicereport)
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
        addControls()
    }

    private fun addControls() {
        val deviceName: TextView = findViewById(R.id.device_name) as TextView
        val deviceImage: ImageView = findViewById(R.id.device_image) as ImageView
        val intent = getIntent()
        val str:String = intent.getStringExtra("value").toString()
        deviceName.text =str
        if (str == "Fans") {
            val toast = Toast.makeText(this, "Viewing fans consumption",Toast.LENGTH_LONG)
            toast.show()
            deviceImage.setImageResource(R.drawable.img_fan)

            var listView = findViewById<ListView>(R.id.lvRoom)
            var list = mutableListOf<RoomReport>()
            list.add(RoomReport("Living Room","3 Hours"))
            list.add(RoomReport("Kitchen","3 Hours"))
            list.add(RoomReport("Bed Room","3 Hours"))
            list.add(RoomReport("Bath Room","3 Hours"))
            list.add(RoomReport("Garage","3 Hours"))

            listView.adapter = MyAdapter(this,R.layout.row,list)

        }
        if (str == "Air-conditioners") {
            val toast = Toast.makeText(this, "Viewing doors consumption",Toast.LENGTH_LONG)
            toast.show()
            deviceImage.setImageResource(R.drawable.img_door)
            var listView = findViewById<ListView>(R.id.lvRoom)
            var list = mutableListOf<RoomReport>()
            list.add(RoomReport("Living Room","3 Hours"))
            list.add(RoomReport("Kitchen","3 Hours"))
            list.add(RoomReport("Bed Room","3 Hours"))
            list.add(RoomReport("Bath Room","3 Hours"))
            list.add(RoomReport("Garage","3 Hours"))

            listView.adapter = MyAdapter(this,R.layout.row,list)
        }
        if (str == "Lights") {
            val toast = Toast.makeText(this, "Viewing lights consumption",Toast.LENGTH_LONG)
            toast.show()
            deviceImage.setImageResource(R.drawable.img_light)

            var listView = findViewById<ListView>(R.id.lvRoom)
            var list = mutableListOf<RoomReport>()
            list.add(RoomReport("Living Room","3 Hours"))
            list.add(RoomReport("Kitchen","3 Hours"))
            list.add(RoomReport("Bed Room","3 Hours"))
            list.add(RoomReport("Bath Room","3 Hours"))
            list.add(RoomReport("Garage","3 Hours"))

            listView.adapter = MyAdapter(this,R.layout.row,list)
        }
    }
}