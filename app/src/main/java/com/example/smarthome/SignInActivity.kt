package com.example.smarthome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.databinding.ActivityAuthenticateBinding

class SignInActivity : AppCompatActivity() {

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
        buttonSignIn.setOnClickListener {
            Log.d("SignIn", "onClick: called")
            val intent = Intent()
            Log.d("SignIn", "Email: ${user_email.text.toString()}")
            Log.d("SignIn", "Password: ${user_password.text.toString()}")

            intent.putExtra("UserEmail", user_email.text.toString())
            intent.putExtra("UserPassword", user_password.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        textViewSignUp.setOnClickListener {
            Log.d("SignUp", "onClick: called")
            val intent = Intent(contxt, SignUpActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }
}