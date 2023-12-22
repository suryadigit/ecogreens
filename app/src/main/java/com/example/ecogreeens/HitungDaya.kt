package com.example.ecogreeens



import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
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
        // Inisialisasi Spinner
        val spinner: Spinner = findViewById(R.id.tarifListrikSpinner)
        val options = arrayOf("900VA", "1300VA")

        // Set adapter untuk Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, options)
        spinner.adapter = adapter

        // Initialize buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val historyButton: ImageButton = findViewById(R.id.historyButton)
        val profileButton: ImageButton = findViewById(R.id.profilebtn)

        homeButton.setOnClickListener {
            goToDasboard()
        }
        // Set click listener for historyButton
        historyButton.setOnClickListener {
            goToHistory()
        }

        // Set click listener for profileButton
        profileButton.setOnClickListener {
            openProfile()
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
        val tarifListrikSpinner = findViewById<Spinner>(R.id.tarifListrikSpinner)

        val dayaListrik = editTextDayaListrik.text.toString().toDoubleOrNull() ?: 0.0
        val tarifPerKwh = editTextTarifKwh.text.toString().toDoubleOrNull() ?: 0.0
        val durasiPemakaian = editTextDurasi.text.toString().toDoubleOrNull() ?: 0.0
        val selectedTarif = tarifListrikSpinner.selectedItem.toString()

        val biayaPerHari: Double
        val biayaTotal: Double

        if (selectedTarif == "900VA") {
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

    @SuppressLint("WrongViewCast")
    fun simpanHasil(view: View) {
        val editTextDayaListrik = findViewById<EditText>(R.id.editTextDayaListrik)
        val editTextTarifKwh = findViewById<EditText>(R.id.editTextTarifKwh)
        val editTextDurasi = findViewById<EditText>(R.id.editTextDurasi)
        val hasilTextView = findViewById<TextView>(R.id.hasilTextView)
        findViewById<RadioButton>(R.id.tarifListrikSpinner)

        val dayaListrik = editTextDayaListrik.text.toString()
        val tarifPerKwh = editTextTarifKwh.text.toString()
        val durasiPemakaian = editTextDurasi.text.toString()
        val hasilPerhitungan = hasilTextView.text.toString()


        // Mendapatkan UID pengguna saat ini
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (!isCalculated) {
            Toast.makeText(this, "Mohon hitung biaya terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
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
    private fun goToHistory() {
        val intent = Intent(this, History::class.java)
        startActivity(intent)
    }
    private fun goToDasboard() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }
    private fun openProfile() {
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }

}

