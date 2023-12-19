    package com.example.ecogreeens

    import android.annotation.SuppressLint
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.widget.Button
    import android.content.Intent
    import com.example.ecogreeens.History
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FirebaseFirestore

    class Dashboard : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth

        @SuppressLint("MissingInflatedId")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_dashboard)

            auth = FirebaseAuth.getInstance()
            FirebaseFirestore.getInstance()

            val btnLogin: Button = findViewById(R.id.btnLogin)
            btnLogin.setOnClickListener {
                startActivity(Intent(this, HitungDaya::class.java))
            }
            val btnLogout: Button = findViewById(R.id.btnLogout)
            btnLogout.setOnClickListener {
                logoutUser() // Panggil fungsi logoutUser saat tombol "Logout" diklik
            }

            val btnHistory: Button = findViewById(R.id.history)
            btnHistory.setOnClickListener {
                goToHistory() // Panggil fungsi goToHistory saat tombol "History" diklik
            }

        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, 0) // Menonaktifkan animasi saat tombol "back" ditekan

    }
        private fun logoutUser() {
            val intent = Intent(this, Logout::class.java)
            startActivity(intent)
            finish() // Selesaikan aktivitas saat melakukan logout untuk kembali ke halaman login
        }

        private fun goToHistory() {
            val intent = Intent(this, History::class.java)
            startActivity(intent)
        }

    }