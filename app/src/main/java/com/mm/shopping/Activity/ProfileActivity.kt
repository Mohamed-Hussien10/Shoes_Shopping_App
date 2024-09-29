package com.mm.shopping.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mm.shopping.Activity.SignUpActivity.User
import com.mm.shopping.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : BaseActivity() {
    private val PICK_IMAGE_REQUEST = 1
    lateinit var id: String
    private lateinit var imageView: ImageView
    private lateinit var database: DatabaseReference

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val selectedImageUri: Uri? = result.data?.data
                imageView.setImageURI(selectedImageUri) // Set the image to the ImageView
                uploadImageToFirebase(selectedImageUri, id)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        database = FirebaseDatabase.getInstance().reference

        val backBtn: ImageView = findViewById(R.id.backBtnProfile)
        backBtn.setOnClickListener {finish()}

        imageView = findViewById(R.id.profile_picture)

        // Set a click listener on the ImageView or button
        imageView.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                checkStoragePermission()
            } else {
                openImagePicker()
            }
        }

        // Setting up the user data
        id = intent.getStringExtra("userId").toString()
        setupProfile(id)

        val showPopupButton: Button = findViewById(R.id.edit_profile_button)
        showPopupButton.setOnClickListener {
            showDialog(it)
        }

        val logout = findViewById<Button>(R.id.logout_button)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    // Set up the profile data
    private fun setupProfile(userId: String) {
        // References to UI elements
        val nameTextView: TextView = findViewById(R.id.user_name)
        val emailTextView: TextView = findViewById(R.id.user_email)
        val profileImageView: ImageView = findViewById(R.id.profile_picture)

        // Get reference to the user's data in the "users" node
        val userReference = database.child("users").child(userId)

        // Attach a listener to read the data
        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve the fields from the snapshot
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val email = dataSnapshot.child("email").getValue(String::class.java)
                    val profileImageUrl =
                        dataSnapshot.child("profileImageUrl").getValue(String::class.java)

                    // Set the data to the respective views
                    nameTextView.text = name ?: "Name not found"
                    emailTextView.text = email ?: "Email not found"

                    // Load the profile image using Glide (or any other image loading library)
                    if (profileImageUrl != null) {
                        Glide.with(this@ProfileActivity)
                            .load(profileImageUrl)
                            .into(profileImageView)
                    }
                } else {
                    // Handle case where the user data doesn't exist
                    nameTextView.text = "User not found"
                    emailTextView.text = ""
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                nameTextView.text = "Error fetching data"
                emailTextView.text = ""
            }
        })
    }

    // Open the image picker using an intent
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent) // Start the image picker
    }

    // Check storage permission (for Android 10 and below)
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            openImagePicker()
        }
    }

    // Handle the permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 101
    }

    // Upload the selected image to Firebase Storage
    private fun uploadImageToFirebase(imageUri: Uri?, id: String) {
        if (imageUri != null) {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("images/${System.currentTimeMillis()}.jpg")

            storageReference.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        // Store the URL in Realtime Database
                        saveImageUrlToDatabase(uri.toString(), id)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseStorage", "Image upload failed", exception)
                }
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String, id: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userId = id

        // Assuming you have a node called "users" in your database
        if (userId != null) {
            databaseReference.child("users").child(userId).child("profileImageUrl")
                .setValue(imageUrl)
                .addOnSuccessListener {
                    Log.d("FirebaseDatabase", "Image URL stored successfully.")
                }
                .addOnFailureListener { exception ->
                    Log.e("FirebaseDatabase", "Failed to store image URL", exception)
                }
        }
    }

    // Show the popup window when the Buttom is clicked
    private fun showDialog(view: View) {
        // Inflate the dialog layout
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.update_profile, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Prevents closing when clicking outside
            .create()

        // Set custom background to the dialog's window
        dialog.window?.setBackgroundDrawableResource(R.drawable.gray_bg)

        // Set up the close button
        val closeButton: Button = dialogView.findViewById(R.id.close_popup_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Set up the update button
        val updateBtn: Button = dialogView.findViewById(R.id.update_profile_button)
        updateBtn.setOnClickListener {
            // Get user inputs
            val name = dialogView.findViewById<EditText>(R.id.update_user)
            val password = dialogView.findViewById<EditText>(R.id.password)
            val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)

            // Validate inputs
            if (validateInputs(
                    name.text.toString(),
                    password.text.toString(),
                    confirmPassword.text.toString(),
                    name,
                    password,
                    confirmPassword
                )
            ) {
                // Proceed with registration logic if validation is successful
                storeUserData(name.text.toString(), id, password.text.toString())
                name.setText("")
                password.setText("")
                confirmPassword.setText("")
                dialog.dismiss()
            }
        }

        // Show the dialog
        dialog.show()
    }


    // Function to validate inputs
    private fun validateInputs(
        name: String,
        password: String,
        confirmPassword: String,
        n:EditText,
        p:EditText,
        cp:EditText
    ): Boolean {
        if (name.isEmpty()) {
            n.error = "Name is required"
            return false
        }

        if (password.isEmpty()) {
            p.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            p.error =
                "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            cp.error = "Confirm your password"
            return false
        }

        if (password != confirmPassword) {
            cp.error = "Passwords do not match"
            return false
        }

        return true
    }

    // Function to storeUserData inputs
    private fun storeUserData(name: String, id: String, password: String): Boolean {
        var flag = false
        // Create a user object
        val user = User(name, password)

        // Save user data to Realtime Database
        database.child("users").child(id).child("name").setValue(name)
        database.child("users").child(id).child("password").setValue(password)
            .addOnSuccessListener {
                Toast.makeText(this, "Update successful!", Toast.LENGTH_SHORT).show()
                flag = true
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error updating user: ${e.message}", Toast.LENGTH_SHORT).show()
                flag = false
            }
        return flag
    }

    data class User(
        val name: String,
        val password: String,
    )
}