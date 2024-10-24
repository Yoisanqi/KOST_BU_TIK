package tiga.kelompok.kostbutik

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class TambahPembayaranActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    private lateinit var etPenyewa: EditText
    private lateinit var etJumlahBayar: EditText
    private lateinit var etTanggalBayar: EditText
    private lateinit var etMetodeBayar: EditText
    private lateinit var etStatus: EditText
    private lateinit var etJatuhTempo: EditText
    private lateinit var btnSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengelola_tambahpembayaran)

        db = FirebaseFirestore.getInstance()

        etPenyewa = findViewById(R.id.spPenyewa)
        etJumlahBayar = findViewById(R.id.edJmlBayar)
        etTanggalBayar = findViewById(R.id.edTanggalBayar)
        etMetodeBayar = findViewById(R.id.edMetodeBayar)
        etStatus = findViewById(R.id.edStatusBayar)
        etJatuhTempo = findViewById(R.id.edJatuhTempo)
        btnSimpan = findViewById(R.id.btnSimpan2)

        btnSimpan.setOnClickListener {
            simpanPembayaran()
        }
    }

    private fun simpanPembayaran() {
        val penyewa = etPenyewa.text.toString()
        val tanggalBayar = etTanggalBayar.text.toString()
        val metodeBayar = etMetodeBayar.text.toString()
        val status = etStatus.text.toString()
        val jatuhTempo = etJatuhTempo.text.toString()

        // Mendapatkan nilai jumlah bayar dengan pengecekan NULL
        val jumlahBayar = etJumlahBayar.text.toString().toDoubleOrNull() ?: 0.0

        if (penyewa.isNotEmpty() && tanggalBayar.isNotEmpty() && metodeBayar.isNotEmpty() && status.isNotEmpty() && jatuhTempo.isNotEmpty()) {
            val pembayaran = hashMapOf(
                "penyewa" to penyewa,
                "jumlahBayar" to jumlahBayar,
                "tanggalBayar" to tanggalBayar,
                "metodeBayar" to metodeBayar,
                "status" to status,
                "jatuhTempo" to jatuhTempo
            )

            // Menyimpan ke Firestore
            db.collection("pembayaran")
                .add(pembayaran)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pembayaran berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                    Toast.makeText(this, "Gagal menyimpan pembayaran", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
        }
    }
}