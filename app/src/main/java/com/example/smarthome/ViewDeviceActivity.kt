package com.example.smarthome

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ViewDeviceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_device)

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener { v: View->
            onBackPressed()
        }
    }

}