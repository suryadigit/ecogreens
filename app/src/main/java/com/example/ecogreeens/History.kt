package com.example.ecogreeens


import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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


//                for (document in documents) {
//                    val dataDayaListrik = document.getString("DayaListrik")
//                    val dataTarifKwh = document.getString("TarifKwh")
//                    val dataDurasi = document.getString("Durasi")
//                    val dataHasil = document.getString("Hasil")
//
//                    // Ambil data waktu dari Firestore
//                    val timestamp = document.getTimestamp("Timestamp")
//
//                    // Format timestamp menjadi tanggal yang diinginkan (misalnya: dd MMMM yyyy)
//                    val formattedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(
//                        timestamp!!.toDate())
//
//                    // Membuat TextView untuk menampilkan data dari Firestore
//                    val textView = TextView(this)
//                    textView.text = "Daya Listrik: $dataDayaListrik\nTarif Kwh: $dataTarifKwh\nDurasi: $dataDurasi\nHasil: $dataHasil\nWaktu: $formattedDate\n\n"
//
//                    // Menambahkan TextView ke dalam LinearLayout
//                    linearLayout.addView(textView)
//                }