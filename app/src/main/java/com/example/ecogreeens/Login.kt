package com.example.ecogreeens


import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecogreeens.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Cek apakah pengguna sudah login sebelumnya
        if (firebaseAuth.currentUser != null) {
            // Pengguna sudah login, arahkan ke halaman Dashboard
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish() // Tidak perlu kembali ke halaman login
        }

        binding.textView.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Di bagian onClickListener untuk tombol Register
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                showLoading(true)

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { loginTask ->
                        showLoading(false)

                        if (loginTask.isSuccessful) {
                            // Login berhasil, arahkan ke halaman Dashboard
                            val intent = Intent(this, Dashboard::class.java)
                            startActivity(intent)
                        } else {
                            val errorMessage = loginTask.exception?.message ?: "Login gagal, coba lagi nanti"
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(this, "Isi semua kolom dengan benar", Toast.LENGTH_SHORT).show()
            }
        }

        val forgotPasswordButton = findViewById<TextView>(R.id.forgotPasswordButton)
        val content = SpannableString(getString(R.string.lupa_password))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)

        forgotPasswordButton.text = content
        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this, ForgetPassword::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(show: Boolean) {
        val loadingOverlay: FrameLayout = findViewById(R.id.loadingOverlay)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        if (show) {
            loadingOverlay.visibility = View.VISIBLE
            // Atur progressBar atau animasi loading lainnya di sini jika diperlukan
        } else {
            loadingOverlay.visibility = View.GONE
            // Sembunyikan progressBar atau animasi loading lainnya di sini jika diperlukan
        }
    }
}
