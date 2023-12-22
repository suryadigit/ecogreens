package com.example.ecogreeens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class Profile : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var profileImage: ImageView
    private var initialProfileImageUri: String? = null
    private val MAX_IMAGE_SIZE_BYTES = 3 * 1024 * 1024
    private val GALLERY_REQUEST_CODE = 123
    private val CAMERA_REQUEST_CODE = 124
    private var isEditModeEnabled = false
    private var profileImageUri: Uri? = null
    // Fungsi untuk memeriksa ukuran gambar
    private fun checkImageSize(uri: Uri): Boolean {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.available() ?: 0
        inputStream?.close()
        return bytes <= MAX_IMAGE_SIZE_BYTES
    }
    // Fungsi untuk mengamati perubahan pada gambar profil
    private fun observeProfileImageChanges() {
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            userDocument.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists() && !isDestroyed) {
                    val profileImageUri = snapshot.getString("profileImageUri")
                    if (!profileImageUri.isNullOrEmpty()) {
                        Glide.with(this@Profile) // Gunakan this@Profile
                            .asBitmap()
                            .load(profileImageUri)
                            .centerCrop()
                            .circleCrop()
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    val circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                                    circularBitmapDrawable.isCircular = true
                                    profileImage.setImageDrawable(circularBitmapDrawable)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {
                                    // Implementasi yang diperlukan saat gambar dihapus dari tampilan
                                }
                            })
                    }
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email
        // Inisialisasi elemen UI
        emailEditText = findViewById(R.id.emailEditText)
        emailEditText.setText(userEmail)
        emailEditText.isEnabled = false

        nameEditText = findViewById(R.id.nameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        addressEditText = findViewById(R.id.addressEditText)
        profileImage = findViewById(R.id.selectImageButton)

        nameEditText.isEnabled = false
        phoneEditText.isEnabled = false
        addressEditText.isEnabled = false
        profileImage.isEnabled = false

        // Mendengarkan tombol saveButton dan editButton
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.visibility = View.GONE
        saveButton.setOnClickListener {
            saveProfileData()
        }

        val editButton = findViewById<ImageButton>(R.id.editProfileButton)
        editButton.setOnClickListener {
            enableEditMode()
        }

        profileImage.setOnClickListener {
            openImageSelection()
        }
        // Initialize buttons
        val hitungDaya: ImageButton = findViewById(R.id.hitungButton)
        val historyButton: ImageButton = findViewById(R.id.historyButton)
        val homeButton : ImageButton = findViewById(R.id.homeButton)

        hitungDaya.setOnClickListener {
            goToHitung()
        }
        // Set click listener for historyButton
        historyButton.setOnClickListener {
            goToHistory()
        }

        // Set click listener for profileButton
        homeButton.setOnClickListener {
            goToBeranda()
        }

        // Memuat data profil saat aktivitas dibuat
        loadProfileData()
        observeProfileImageChanges()
    }

    // Fungsi untuk mengaktifkan mode edit
    private fun enableEditMode() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        isEditModeEnabled = true
        nameEditText.isEnabled = true
        phoneEditText.isEnabled = true
        addressEditText.isEnabled = true

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.visibility = View.VISIBLE

        profileImage.isEnabled = true
        profileImage.setOnClickListener {
            openImageSelection()
        }
    }

    // Fungsi untuk menyimpan data profil
// Fungsi untuk menyimpan data profil
    private fun saveProfileData() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        isEditModeEnabled = false
        nameEditText.isEnabled = false
        phoneEditText.isEnabled = false
        addressEditText.isEnabled = false
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.visibility = View.GONE
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
            if (profileImageUri != null && profileImageUri.toString() != initialProfileImageUri) {
                userData["profileImageUri"] = profileImageUri.toString()
                uploadImageToFirebaseStorage()
            }
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


    // Fungsi untuk membuka pemilihan gambar
    private fun openImageSelection() {
        val selectImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val captureImageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val chooser = Intent.createChooser(selectImageIntent, "Pilih Foto Profil")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureImageIntent))

        startActivityForResult(chooser, GALLERY_REQUEST_CODE)
    }

    // Fungsi untuk menangani hasil dari pemilihan gambar/kamera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val imageData: Uri = data.data ?: return
                    profileImage.setImageURI(imageData)
                    profileImageUri = imageData
                    uploadImageToFirebaseStorage()
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.extras != null) {
                    val imageBitmap: Bitmap = data.extras!!.get("data") as Bitmap
                    profileImage.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    // Fungsi untuk mengunggah gambar ke Firebase Storage
    private fun uploadImageToFirebaseStorage() {
        profileImageUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images")
            val imageRef = storageRef.child("${auth.currentUser?.uid}.jpg")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        profileImageUri = uri
                        updateProfileImageUri(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Fungsi untuk memperbarui URI gambar profil di Firestore
    private fun updateProfileImageUri(uri: String) {
        val db = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)
            userDocument.update("profileImageUri", uri)
                .addOnSuccessListener {
                    Toast.makeText(this, "URI gambar berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memperbarui URI gambar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Fungsi untuk memuat data profil
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

                        val profileImageUri = userData?.get("profileImageUri") as? String
                        if (!profileImageUri.isNullOrEmpty()) {
                            initialProfileImageUri = profileImageUri


                            // Load gambar profil dari URL Firebase ke ImageView
                            Glide.with(this)
                                .asBitmap()
                                .load(profileImageUri)
                                .centerCrop()
                                .circleCrop()
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        val circularBitmapDrawable: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, resource)
                                        circularBitmapDrawable.isCircular = true
                                        profileImage.setImageDrawable(circularBitmapDrawable)
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {
                                    }
                                })
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onBackPressed() {
        if (isEditModeEnabled) {
            Toast.makeText(this, "Simpan perubahan sebelum kembali", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }
    fun logoutUser(view: View) {
        FirebaseAuth.getInstance().signOut()

        // Redirect ke layar login
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun goToBeranda() {
        val profileIntent = Intent(this, Dashboard::class.java)
        startActivityForResult(profileIntent, Dashboard.PROFILE_REQUEST_CODE)
    }
    private fun goToHistory() {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
    }
    private fun goToHitung() {
        val intent = Intent(this, HitungDaya::class.java)
        startActivity(intent)
    }
}
