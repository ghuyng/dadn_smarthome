package com.example.smarthome

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetAutoModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_auto_mode)

        val confirm = findViewById<TextView>(R.id.setting_auto_confirm)
        val setDefault = findViewById<TextView>(R.id.setting_auto_set_default)
        findViewById<ImageButton>(R.id.setting_auto_back_button).setOnClickListener { onBackPressed()}


        setDefault.setOnClickListener {
            Toast.makeText(this, "Set Default!!!", Toast.LENGTH_SHORT).show()
        }
        confirm.setOnClickListener {
            Toast.makeText(this,"Confirm!!!", Toast.LENGTH_SHORT).show()
        }
    }
}