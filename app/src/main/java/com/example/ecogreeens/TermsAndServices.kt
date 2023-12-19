package com.example.ecogreeens

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TermsAndServices : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set layout atau inisialisasi tampilan yang diperlukan jika ada
        setContentView(R.layout.activity_terms)

        // Tambahkan fungsi untuk menutup aktivitas saat teks diklik
        findViewById<TextView>(R.id.acceptButton).setOnClickListener {
            finish() // Menutup aktivitas ini dan kembali ke aktivitas sebelumnya
        }
    }
}
    class PrivacyPolicy : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_privacy)

            findViewById<TextView>(R.id.acceptButton).setOnClickListener {
                finish() // Menutup aktivitas ini dan kembali ke aktivitas sebelumnya
            }
    }
}