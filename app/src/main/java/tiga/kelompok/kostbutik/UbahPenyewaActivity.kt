package tiga.kelompok.kostbutik

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class UbahPenyewaActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var imageUri: Uri

    // UI elements
    private lateinit var edNamaU: EditText
    private lateinit var edNikU: EditText
    private lateinit var edAlamatU: EditText
    private lateinit var edTeleponU: EditText
    private lateinit var edEmailU: EditText
    private lateinit var edTanggalU: EditText
    private lateinit var edKamarU: EditText
    private lateinit var edStatusU: EditText
    private lateinit var edFotoU: EditText
    private lateinit var btnUbah: Button
    private lateinit var imvFotoU: ImageView

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengelola_ubahpenyewa)

        // Initialize Firestore and Storage
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Initialize UI elements
        edNamaU = findViewById(R.id.edNamaU)
        edNikU = findViewById(R.id.edNikU)
        edAlamatU = findViewById(R.id.edAlamatU)
        edTeleponU = findViewById(R.id.edTeleponU)
        edEmailU = findViewById(R.id.edEmailU)
        edTanggalU = findViewById(R.id.edTanggalU)
        edKamarU = findViewById(R.id.edKamarU)
        edStatusU = findViewById(R.id.edStatusU)
        edFotoU = findViewById(R.id.edFotoU)
        btnUbah = findViewById(R.id.btnUbah1)
        imvFotoU = findViewById(R.id.imvFotoU)

        val renterId = intent.getStringExtra("id_penyewa")

        if (renterId != null) {
            loadRenterData(renterId)
        }

        imvFotoU.setOnClickListener {
            openGallery()
        }

        btnUbah.setOnClickListener {
            if (renterId != null) {
                if (::imageUri.isInitialized) {
                    uploadImageToStorage(renterId)
                } else {
                    updateRenterData(renterId)
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            imvFotoU.setImageURI(imageUri) // Set image to ImageView

            // Dapatkan nama file asli
            val fileName = getFileNameFromUri(imageUri)
            if (fileName != null) {
                edFotoU.setText(fileName) // Tampilkan nama file asli di EditText
            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex)
            }
            cursor.close()
        }
        return fileName
    }

    private fun uploadImageToStorage(renterId: String) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now) + ".jpg"
        val storageRef = storage.reference.child("images/$fileName")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    edFotoU.setText(uri.toString())
                    updateRenterData(renterId) // Update data after image upload
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error uploading image: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRenterData(renterId: String) {
        if (isInputValid()) {
            val updatedData = hashMapOf<String, Any>(
                "nama" to edNamaU.text.toString(),
                "nik" to edNikU.text.toString(),
                "alamat" to edAlamatU.text.toString(),
                "telepon" to edTeleponU.text.toString(),
                "email" to edEmailU.text.toString(),
                "tanggal_masuk" to edTanggalU.text.toString(),
                "kamar" to edKamarU.text.toString(),
                "status" to edStatusU.text.toString(),
                "foto_ktp" to edFotoU.text.toString()
            )

            db.collection("penyewa").document(renterId)
                .update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Renter data updated successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error updating renter data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isInputValid(): Boolean {
        return if (edNamaU.text.isEmpty() || edNikU.text.isEmpty() || edAlamatU.text.isEmpty() ||
            edTeleponU.text.isEmpty() || edEmailU.text.isEmpty() || edTanggalU.text.isEmpty() ||
            edKamarU.text.isEmpty() || edStatusU.text.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }

    private fun loadRenterData(renterId: String) {
        db.collection("penyewa").document(renterId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    edNamaU.setText(document.getString("nama"))
                    edNikU.setText(document.getString("nik"))
                    edAlamatU.setText(document.getString("alamat"))
                    edTeleponU.setText(document.getString("telepon"))
                    edEmailU.setText(document.getString("email"))
                    edTanggalU.setText(document.getString("tanggal_masuk"))
                    edKamarU.setText(document.getString("kamar"))
                    edStatusU.setText(document.getString("status"))
                    edFotoU.setText(document.getString("foto_ktp"))
                } else {
                    Toast.makeText(this, "Renter not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error loading renter data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}