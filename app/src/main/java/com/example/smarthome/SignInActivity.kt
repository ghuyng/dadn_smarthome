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
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ulSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticateBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        val contxt = this
        binding = ActivityAuthenticateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user_email: EditText = findViewById(R.id.editTextTextEmailAddress)
        val user_password: EditText = findViewById(R.id.editTextTextPassword)
        val buttonSignIn: Button = findViewById(R.id.button_sign_in)
        val textViewSignUp: TextView = findViewById(R.id.signUpTextView)
        buttonSignIn.setOnClickListener {
            Log.d("SignIn", "onClick: called")
            Log.d("SignIn", "Email: ${user_email.text.toString()}")
            Log.d("SignIn", "Password: ${user_password.text.toString()}")
            signIn(user_email.text.toString(), user_password.text.toString())
        }
        textViewSignUp.setOnClickListener {
            Log.d("SignUp", "onClick: called")
            val intent = Intent(contxt, SignUpActivity::class.java)
            startActivityForResult(intent, 200)
        }
    }
    private fun signIn(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                Log.d("SignIn", "signInWithEmail called" )
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignIn", "signInWithEmail:success")
                    val intent = Intent()
                    intent.putExtra("UserEmail", email)
                    intent.putExtra("UserPassword", password)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignIn", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed :(",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}