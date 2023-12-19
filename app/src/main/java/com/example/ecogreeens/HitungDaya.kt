package com.example.ecogreeens



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.api.ResourceDescriptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HitungDaya : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isCalculated = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hitungdaya)

        auth = FirebaseAuth.getInstance()
        FirebaseFirestore.getInstance()

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun logoutUser() {
        auth.signOut()

        // Redirect to login screen after logout
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }



    fun hitungBiaya(view: View) {
        val editTextDayaListrik = findViewById<EditText>(R.id.editTextDayaListrik)
        val editTextTarifKwh = findViewById<EditText>(R.id.editTextTarifKwh)
        val editTextDurasi = findViewById<EditText>(R.id.editTextDurasi)
        val hasilTextView = findViewById<TextView>(R.id.hasilTextView)
        val tarif900vaRadioButton = findViewById<RadioButton>(R.id.tarif900vaRadioButton)

        val dayaListrik = editTextDayaListrik.text.toString().toDoubleOrNull() ?: 0.0
        val tarifPerKwh = editTextTarifKwh.text.toString().toDoubleOrNull() ?: 0.0
        val durasiPemakaian = editTextDurasi.text.toString().toDoubleOrNull() ?: 0.0

        val biayaPerHari: Double
        val biayaTotal: Double

        if (tarif900vaRadioButton.isChecked) {
            biayaPerHari = dayaListrik * tarifPerKwh
            biayaTotal = biayaPerHari * durasiPemakaian
        } else {
            // Hitung untuk 1300VA
            // Lakukan perhitungan sesuai dengan logika yang diinginkan
            biayaPerHari = dayaListrik * tarifPerKwh * 1.2 // Contoh: 20% lebih tinggi dari 900VA
            biayaTotal = biayaPerHari * durasiPemakaian
        }

        hasilTextView.text = "Biaya Listrik Per Hari: $biayaTotal rupiah"

        isCalculated = true

}
    fun simpanHasil(view: View) {
        val editTextDayaListrik = findViewById<EditText>(R.id.editTextDayaListrik)
        val editTextTarifKwh = findViewById<EditText>(R.id.editTextTarifKwh)
        val editTextDurasi = findViewById<EditText>(R.id.editTextDurasi)
        val hasilTextView = findViewById<TextView>(R.id.hasilTextView)
        val tarif900vaRadioButton = findViewById<RadioButton>(R.id.tarif900vaRadioButton)

        val dayaListrik = editTextDayaListrik.text.toString()
        val tarifPerKwh = editTextTarifKwh.text.toString()
        val durasiPemakaian = editTextDurasi.text.toString()
        val hasilPerhitungan = hasilTextView.text.toString()

        // Mendapatkan UID pengguna saat ini
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        // Lakukan validasi di sini, memastikan semua field terisi dan ada UID pengguna
        if (isCalculated && uid != null && dayaListrik.isNotEmpty() && tarifPerKwh.isNotEmpty() && durasiPemakaian.isNotEmpty() && hasilPerhitungan.isNotEmpty()) {

            // Menyimpan data ke Firestore berdasarkan UID pengguna
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(uid)
            val data = hashMapOf(
                "DayaListrik" to dayaListrik,
                "TarifKwh" to tarifPerKwh,
                "Durasi" to durasiPemakaian,
                "Hasil" to hasilPerhitungan
            )

            userDocRef.collection("history").add(data)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Berhasil menyimpan data", Toast.LENGTH_SHORT).show()
                    // Berhasil menyimpan data ke Firestore
                    // Lakukan navigasi ke halaman History setelah data berhasil disimpan
                    val intent = Intent(this, History::class.java).apply {
                        putExtra("DayaListrik", dayaListrik)
                        putExtra("TarifKwh", tarifPerKwh)
                        putExtra("Durasi", durasiPemakaian)
                        putExtra("Hasil", hasilPerhitungan)
                    }
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                    // Gagal menyimpan data ke Firestore
                    // Tindakan penanganan kesalahan jika diperlukan
                }
        } else {
            // Tampilkan pesan kepada pengguna bahwa semua field harus diisi
            Toast.makeText(this, "Periksa data dan hasilnya", Toast.LENGTH_SHORT).show()
        }
    }

}

