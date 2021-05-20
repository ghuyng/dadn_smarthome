package com.example.smarthome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.content.Intent
import android.widget.TextView
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.smarthome.databinding.ActivityAuthenticateBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contxt = this
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val button: Button = findViewById(R.id.button_sign_in)
        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("SignIn", "onClick: called")
                val intent = Intent(contxt, SignUpActivity::class.java)
                startActivity(intent)
            }
        })
    }
}