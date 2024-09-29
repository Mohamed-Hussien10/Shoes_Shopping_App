package com.mm.shopping.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mm.shopping.R

class LoginActivity : AppCompatActivity() {

    private lateinit var realtimeDb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        realtimeDb = FirebaseDatabase.getInstance().reference.child("users")

        val signInButton: Button = findViewById(R.id.loginButton)
        signInButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordInput).text.toString()

            if (validateInputs(email, password)) {
                signInUser(email, password)
            }
        }

        val signupText = findViewById<TextView>(R.id.signupText)
        signupText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            findViewById<EditText>(R.id.emailInput).error = "Email is required"
            return false
        }
        if (password.isEmpty()) {
            findViewById<EditText>(R.id.passwordInput).error = "Password is required"
            return false
        }
        return true
    }

    private fun signInUser(email: String, password: String) {
        var userId = ""
        realtimeDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var foundUser = false
                for (userSnapshot in dataSnapshot.children) {
                    val dbEmail = userSnapshot.child("email").value.toString()
                    val dbPassword = userSnapshot.child("password").value.toString()

                    if (dbEmail == email && dbPassword == password) {
                        foundUser = true
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                            .show()
                        userId = userSnapshot.child("userId").value.toString()
                        // Navigate to another activity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("userId", userId)
                        startActivity(intent)
                        finish()
                        break
                    }
                }
                if (!foundUser) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Invalid email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}