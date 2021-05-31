package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.smarthome.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
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
            val intent = Intent(contxt, MainActivity::class.java)
            Log.d("SignUp", "Name: ${user_name.text.toString()}")
            Log.d("SignUp", "Email: ${user_email.text.toString()}")
            Log.d("SignUp", "Password: ${user_password.text.toString()}")
            Log.d("SignUp", "Password Retype: ${user_passwordRe.text.toString()}")
            if (user_password.text.toString() != user_passwordRe.text.toString()){
                Toast.makeText(baseContext, "Password reconfirm mismatch",
                    Toast.LENGTH_SHORT).show()
            } else {
                createAccount(user_email.text.toString(), user_password.text.toString())
            }
//            intent.putExtra("NewUserName", user_name.toString())
//            intent.putExtra("NewUserEmail", user_email.toString())
//            intent.putExtra("NewUserPassword", user_password.toString())
//            intent.putExtra("NewUserPasswordRetype", user_passwordRe.toString())
//            finish()
        }
        buttonBack.setOnClickListener {
            Log.d("SignUpBack", "onClick: called")
            finish()
        }
    }
    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignUp", "createUserWithEmail:success")
                    //intent.putExtra("NewUserName", user_name.toString())
                    intent.putExtra("NewUserEmail", email)
                    intent.putExtra("NewUserPassword", password)
                    //intent.putExtra("NewUserPasswordRetype", user_passwordRe.toString())
                    Toast.makeText(baseContext, "Account created.",
                        Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignUp", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }
}