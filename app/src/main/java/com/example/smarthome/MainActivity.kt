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
        val user_email: EditText = findViewById(R.id.editTextTextEmailAddress)
        val user_password: EditText = findViewById(R.id.editTextTextPassword)
        val buttonSignIn: Button = findViewById(R.id.button_sign_in)
        val textViewSignUp: TextView = findViewById(R.id.signUpTextView)
        buttonSignIn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("SignIn", "onClick: called")
                val intent = Intent(contxt, SignUpActivity::class.java)
                Log.d("SignIn", "Email: ${user_email.text.toString()}")
                Log.d("SignIn", "Password: ${user_password.text.toString()}")

                intent.putExtra("UserEmail",user_email.toString())
                intent.putExtra("UserPassword",user_password.toString())
                // startActivity(intent)
            }
        })
        textViewSignUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Log.d("SignUp", "onClick: called")
                val intent = Intent(contxt, SignUpActivity::class.java)
                startActivity(intent)
            }
        })
    }
}