package com.example.ecogreeens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ecogreeens.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth


class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val agreeChecked = binding.agreeCheckBox.isChecked // Mendapatkan status checkbox

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    if (agreeChecked) {
                        showLoading(true)
                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener {
                                showLoading(false)
                                if (it.isSuccessful) {
                                    // Registrasi berhasil, arahkan ke halaman login
                                    val intent = Intent(this, Login::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Syarat dan ketentuan berlaku. Baca terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Isi semua kolom dengan benar", Toast.LENGTH_SHORT).show()
            }
        }


        // Fungsi onClick untuk menavigasi ke halaman TermsAndServices saat TextView 'terms' diklik
        binding.terms.setOnClickListener {
            val intent = Intent(this, TermsAndServices::class.java)
            startActivity(intent)
            // Anda juga dapat menambahkan animasi transisi di sini jika diinginkan
        }

        // Fungsi onClick untuk menavigasi ke halaman TermsAndServices saat TextView 'privacy' diklik
        binding.privacy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicy::class.java)
            startActivity(intent)
            // Anda juga dapat menambahkan animasi transisi di sini jika diinginkan
        }

    }
    private fun showLoading(show: Boolean) {
        val progressBar = binding.progressBar
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
