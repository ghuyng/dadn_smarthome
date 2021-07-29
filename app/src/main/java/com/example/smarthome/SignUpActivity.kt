package com.example.smarthome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthome.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var dbcontext: DatabaseReference
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)
        val buttonSignUp: Button = findViewById(R.id.btnSignUp)
        val buttonBack: ImageButton = findViewById(R.id.btnBackToSignIn)
        val user_name: EditText = findViewById(R.id.textNameSignUp)
        val user_email: EditText = findViewById(R.id.textEmailSignUp)
        val user_password: EditText = findViewById(R.id.textPasswordSignUp)
        val user_passwordRe: EditText = findViewById(R.id.textPasswordRetypeSignUp)
        buttonSignUp.setOnClickListener {
            Log.d("SignUp", "onClick: called")
            Log.d("SignUp", "Name: ${user_name.text.toString()}")
            Log.d("SignUp", "Email: ${user_email.text.toString()}")
            Log.d("SignUp", "Password: ${user_password.text.toString()}")
            Log.d("SignUp", "Password Retype: ${user_passwordRe.text.toString()}")
            if (user_password.text.toString() != user_passwordRe.text.toString()){
                Toast.makeText(baseContext, "Password reconfirmation mismatch",
                    Toast.LENGTH_SHORT).show()
            } else {
                val email_str = user_email.text.toString()
                val password_str = user_password.text.toString()
                val name_str = user_name.text.toString()
                //checkAndAddFirebase(email_str, name_str)
                if (email_str.isNullOrEmpty() || password_str.isNullOrEmpty() || name_str.isNullOrEmpty()) {
                    Toast.makeText(baseContext, "Empty field(s)",
                        Toast.LENGTH_SHORT).show()
                } else {
                    checkAndAddFirebase(email_str, name_str, password_str)
                }
            }
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

                    //intent.putExtra("NewUserPasswordRetype", user_passwordRe.toString())
                    Toast.makeText(baseContext, "Account created.",
                        Toast.LENGTH_SHORT).show()
                    val data = Intent()
                    data.putExtra("NewUserEmail", email)
                    data.putExtra("NewUserPassword", password)
                    setResult(RESULT_OK, data)
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
    private fun checkAndAddFirebase(email: String, name: String, password: String){
        dbcontext = FirebaseDatabase.getInstance().reference.root
        dbcontext.child("Account").get().addOnSuccessListener { snapshot ->
            Log.d("Firebase call","Success")
            if (email in snapshot.children.map{it.child("Email").value}) {
                Toast.makeText(baseContext, "User with this email already exists",
                    Toast.LENGTH_SHORT).show()
            } else {
                val nameRef = dbcontext.child("Account")
                val key: String = nameRef.push().key!!
                nameRef.child(key).child("Email").setValue(email)
                nameRef.child(key).child("Name").setValue(name)
                createAccount(email, password)
            }
        }.addOnFailureListener {
            Log.d("Firebase call","failed at email")
        }
    }
}