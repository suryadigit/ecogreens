package com.example.ecogreeens


import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class History : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val linearLayout = findViewById<LinearLayout>(R.id.linear_layout) // LinearLayout sebagai wadah TextView



        // Mendapatkan UID pengguna saat ini
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid != null) {
            // Menghubungkan dengan Firestore
            val db = FirebaseFirestore.getInstance()

            // Mendapatkan koleksi history dari Firestore berdasarkan UID pengguna
            db.collection("users").document(uid).collection("history").get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        // Ambil data dari Firestore
                        val dataDayaListrik = document.getString("DayaListrik")
                        val dataTarifKwh = document.getString("TarifKwh")
                        val dataDurasi = document.getString("Durasi")
                        val dataHasil = document.getString("Hasil")

                        // Membuat TextView baru untuk menampilkan data dari Firestore
                        val textView = TextView(this)
                        textView.text = "Daya Listrik: $dataDayaListrik\nTarif Kwh: $dataTarifKwh\nDurasi: $dataDurasi\nHasil: $dataHasil\n\n"

                        // Menambahkan TextView ke dalam LinearLayout
                        linearLayout.addView(textView)
                    }
                }
                .addOnFailureListener { exception ->
                    // Tangani kegagalan pengambilan data jika diperlukan
                }
        }

    }

}


