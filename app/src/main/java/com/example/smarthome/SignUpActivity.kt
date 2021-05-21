package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.smarthome.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val contxt = this
        val buttonSignUp: Button = findViewById(R.id.btnSignUp)
        val buttonBack: Button = findViewById(R.id.btnBackToSignIn)
        val user_name: EditText = findViewById(R.id.textNameSignUp)
        val user_email: EditText = findViewById(R.id.textEmailSignUp)
        val user_password: EditText = findViewById(R.id.textPasswordSignUp)
        val user_passwordRe: EditText = findViewById(R.id.textPasswordRetypeSignUp)
        buttonSignUp.setOnClickListener {
            Log.d("SignUp", "onClick: called")
            val intent = Intent(contxt, SignInActivity::class.java)
            Log.d("SignUp", "Name: ${user_name.text.toString()}")
            Log.d("SignUp", "Email: ${user_email.text.toString()}")
            Log.d("SignUp", "Password: ${user_password.text.toString()}")
            Log.d("SignUp", "Password Retype: ${user_passwordRe.text.toString()}")

            intent.putExtra("NewUserName", user_name.toString())
            intent.putExtra("NewUserEmail", user_email.toString())
            intent.putExtra("NewUserPassword", user_password.toString())
            intent.putExtra("NewUserPasswordRetype", user_passwordRe.toString())
            // startActivity(intent)
        }
        buttonBack.setOnClickListener {
            Log.d("SignUpBack", "onClick: called")
            val intent = Intent(contxt, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}