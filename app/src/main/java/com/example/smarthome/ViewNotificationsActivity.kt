package com.example.smarthome

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ViewNotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notice)

        findViewById<MaterialButton>(R.id.btn_stop).setOnClickListener {stopAlarm()}
    }

    private fun stopAlarm() {
        val apiController = APIController(this)
        apiController.jsonObjectGET("/stop-alert") { res ->
            Log.d("POST stop-alert", res.toString())
            if (res?.get("message") != "good") {
                Toast.makeText(this, "Something went wrong. Please retry again sometime", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Alarm stopped", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

}