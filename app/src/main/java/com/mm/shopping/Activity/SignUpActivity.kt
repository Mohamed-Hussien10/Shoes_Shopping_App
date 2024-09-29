package com.mm.shopping.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mm.shopping.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SignUpActivity : BaseActivity() {
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        // Set up click listener for the "Sign Up" button
        val signupButton: Button = findViewById(R.id.signupButton)
        signupButton.setOnClickListener {
            // Get user inputs
            var name = findViewById<EditText>(R.id.nameInput)
            val email = findViewById<EditText>(R.id.emailInput)
            val password = findViewById<EditText>(R.id.passwordInput)
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordInput)

            // Validate inputs
            if (validateInputs(name.text.toString(), email.text.toString(), password.text.toString(), confirmPassword.text.toString())) {
                // Proceed with registration logic if validation is successful
                storeUserData(name.text.toString(), email.text.toString(), password.text.toString())
                name.setText("")
                email.setText("")
                password.setText("")
                confirmPassword.setText("")
            }
        }

        val loginText: TextView = findViewById(R.id.loginText)
        loginText.setOnClickListener {
            // Navigate to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {finish()}

    }

    // Function to validate inputs
    private fun validateInputs(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            findViewById<EditText>(R.id.nameInput).error = "Name is required"
            return false
        }

        if (email.isEmpty()) {
            findViewById<EditText>(R.id.emailInput).error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            findViewById<EditText>(R.id.emailInput).error = "Enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            findViewById<EditText>(R.id.passwordInput).error = "Password is required"
            return false
        }

        if (password.length < 6) {
            findViewById<EditText>(R.id.passwordInput).error =
                "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            findViewById<EditText>(R.id.confirmPasswordInput).error = "Confirm your password"
            return false
        }

        if (password != confirmPassword) {
            findViewById<EditText>(R.id.confirmPasswordInput).error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun storeUserData(name: String, email: String, password: String):Boolean {
        var flag = false
        // Create a signup date
        val signupDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Create a user object
        val userId = database.child("users").push().key // Generate unique ID for the user
        val user = User(userId!!, name, email, password, signupDate)

        // Save user data to Realtime Database
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                flag = true
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
                flag = false
            }
        return flag
    }

    // User data class
    data class User(
        val userId: String,
        val name: String,
        val email: String,
        val password: String,
        val signupDate: String
    )
}

