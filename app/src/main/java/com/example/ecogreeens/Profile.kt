package com.example.ecogreeens

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private var isEditModeEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        emailEditText = findViewById(R.id.emailEditText)
        emailEditText.setText(userEmail)
        emailEditText.isEnabled = false

        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)

        val editButton: Button = findViewById(R.id.editButton)
        editButton.setOnClickListener {
            enableEditMode()
        }

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProfileData()
        }
    }

    private fun enableEditMode() {
        isEditModeEnabled = true
        nameEditText.isEnabled = true
        phoneEditText.isEnabled = true
        addressEditText.isEnabled = true

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.visibility = View.VISIBLE
    }

    private fun saveProfileData() {
        isEditModeEnabled = false
        nameEditText.isEnabled = false
        phoneEditText.isEnabled = false
        addressEditText.isEnabled = false

        val newName = nameEditText.text.toString().trim()
        val newPhone = phoneEditText.text.toString().trim()
        val newAddress = addressEditText.text.toString().trim()

        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            val userData = hashMapOf(
                "name" to newName,
                "phone" to newPhone,
                "address" to newAddress
            )

            userDocument.set(userData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    val saveButton: Button = findViewById(R.id.saveButton)
                    saveButton.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun loadProfileData() {
        val currentUser = auth.currentUser
        val db = FirebaseFirestore.getInstance()
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            userDocument.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userData = document.data
                        nameEditText.setText(userData?.get("name") as? String ?: "")
                        phoneEditText.setText(userData?.get("phone") as? String ?: "")
                        addressEditText.setText(userData?.get("address") as? String ?: "")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }

}

