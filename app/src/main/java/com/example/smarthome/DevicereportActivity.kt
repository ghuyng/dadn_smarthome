package com.example.smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class DevicereportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devicereport)
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
        }
        if (str == "Air-conditioners") {
            val toast = Toast.makeText(this, "Viewing AC consumption",Toast.LENGTH_LONG)
            toast.show()
            deviceImage.setImageResource(R.drawable.img_fan)
        }
        if (str == "Lights") {
            val toast = Toast.makeText(this, "Viewing lights consumption",Toast.LENGTH_LONG)
            toast.show()
            deviceImage.setImageResource(R.drawable.img_fan)
        }
    }
}