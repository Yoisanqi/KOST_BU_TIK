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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UbahPembayaranActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var pembayaranId: String

    private lateinit var etPenyewa: EditText
    private lateinit var etJumlahBayar: EditText
    private lateinit var etTanggalBayar: EditText
    private lateinit var etMetodeBayar: EditText
    private lateinit var etStatus: EditText
    private lateinit var etJatuhTempo: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengelola_ubahpembayaran)

        db = Firebase.firestore

        etPenyewa = findViewById(R.id.spPenyewaU)
        etJumlahBayar = findViewById(R.id.edJmlBayarU)
        etTanggalBayar = findViewById(R.id.edTanggalBayarU)
        etMetodeBayar = findViewById(R.id.edMetodeBayarU)
        etStatus = findViewById(R.id.edStatusBayarU)
        etJatuhTempo = findViewById(R.id.edJatuhTempoU)
        btnUpdate = findViewById(R.id.btnUbah2)

        pembayaranId = intent.getStringExtra("PEMBAYARAN_ID") ?: ""

        loadPembayaranData()

        btnUpdate.setOnClickListener {
            updatePembayaran()
        }
    }

    private fun loadPembayaranData() {
        db.collection("pembayaran").document(pembayaranId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    etPenyewa.setText(document.getString("penyewa"))
                    etJumlahBayar.setText(document.getDouble("jumlahBayar")?.toString())
                    etTanggalBayar.setText(document.getString("tanggalBayar"))
                    etMetodeBayar.setText(document.getString("metodeBayar"))
                    etStatus.setText(document.getString("status"))
                    etJatuhTempo.setText(document.getString("jatuhTempo"))
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePembayaran() {
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

            // Memperbarui data di Firestore
            db.collection("pembayaran").document(pembayaranId) // idPembayaran adalah ID dokumen yang ingin diperbarui
                .set(pembayaran)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pembayaran berhasil diubah", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                    Toast.makeText(this, "Gagal mengubah pembayaran", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
        }
    }

}
