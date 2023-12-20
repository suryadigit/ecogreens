package com.example.ecogreeens

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Dashboard : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance()

        val btnHitungDaya: Button = findViewById(R.id.btnHitung)
        btnHitungDaya.setOnClickListener {
            startActivity(Intent(this, HitungDaya::class.java))
        }

        val btnProfile: Button = findViewById(R.id.btnProfile)
        btnProfile.setOnClickListener {
            startActivity(Intent(this, com.example.ecogreeens.Profile::class.java))
        }
        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logoutUser() // Panggil fungsi logoutUser saat tombol "Logout" diklik
        }

        val btnHistory: Button = findViewById(R.id.history)
        btnHistory.setOnClickListener {
            goToHistory() // Panggil fungsi goToHistory saat tombol "History" diklik
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0) // Menonaktifkan animasi saat tombol "back" ditekan
    }

    private fun goToHistory() {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
    }

    private fun logoutUser() {
        val intent = Intent(this, Logout::class.java)
        startActivity(intent)
        finish() // Selesaikan aktivitas saat melakukan logout untuk kembali ke halaman login
    }

}
