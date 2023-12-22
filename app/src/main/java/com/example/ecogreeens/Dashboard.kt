package com.example.ecogreeens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var profileImageUri: String? = null
    private lateinit var profileImage: ImageView
    private lateinit var usernameTextView: TextView
    companion object {
        const val PROFILE_REQUEST_CODE = 123
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance()
        profileImage = findViewById(R.id.profileImage)
        usernameTextView = findViewById(R.id.usernameTextView)

        // Initialize buttons
        val hitungDaya: ImageButton = findViewById(R.id.hitungButton)
        val historyButton: ImageButton = findViewById(R.id.historyButton)
        val profileButton: ImageButton = findViewById(R.id.profilebtn)

        hitungDaya.setOnClickListener {
            goToHitung()
        }
        // Set click listener for historyButton
        historyButton.setOnClickListener {
            goToHistory()
        }

        // Set click listener for profileButton
        profileButton.setOnClickListener {
            openProfile()
        }
    }
    override fun onResume() {
        super.onResume()
        loadProfileImage()
        loadUsername()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                loadProfileImage()
                loadUsername()
                // Lakukan apa pun yang perlu diperbarui di Dashboard setelah profil diupdate
            }
        }

    }



    private fun loadProfileImage() {
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data // Ambil data user dari Firestore
                        val username = userData?.get("name") as? String ?: "" // Ambil nama pengguna dari data user
                        usernameTextView.text = username // Set nama pengguna ke TextView

                        profileImageUri = document.getString("profileImageUri")
                        profileImageUri?.let {
                            Glide.with(this)
                                .load(profileImageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(profileImage)
                        } ?: run {
                            profileImage.setImageResource(R.drawable.default_profile_image)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle error saat mengambil data gambar dari Firebase
                }
        }
    }



    private fun loadUsername() {
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username")
                        username?.let {
                            usernameTextView.text = it
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle error saat mengambil data nama pengguna dari Firestore
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0)
    }

    // Navigate to History activity
    private fun goToHistory() {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
    }
    private fun goToHitung() {
        val intent = Intent(this, HitungDaya::class.java)
        startActivity(intent)
    }
    // Navigate to Profile activity
    private fun openProfile() {
        val profileIntent = Intent(this, Profile::class.java)
        startActivityForResult(profileIntent, PROFILE_REQUEST_CODE)
    }
}