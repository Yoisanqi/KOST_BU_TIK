package tiga.kelompok.kostbutik

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class TambahPenyewaActivity : AppCompatActivity() {

    // Firestore and Storage instances
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var edFoto: EditText
    private lateinit var imvGambar: ImageView

    // Request code for image selection
    private val IMAGE_PICK_CODE = 1000

    // Variable for auto-generated id_penyewa
    private lateinit var idPenyewa: String
    private var selectedImageUri: Uri? = null // Store the selected image URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pengelola_tambahpenyewa)

        // Initialize Firestore and Storage
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Generate random id_penyewa with prefix "P"
        idPenyewa = generateRandomIdPenyewa()

        // Get references to the form fields
        val edNama = findViewById<EditText>(R.id.edNama)
        val edNik = findViewById<EditText>(R.id.edNik)
        val edAlamat = findViewById<EditText>(R.id.edAlamat)
        val edTelepon = findViewById<EditText>(R.id.edTelepon)
        val edEmail = findViewById<EditText>(R.id.edEmailT)
        val edPassword = findViewById<EditText>(R.id.edPasswordT)
        val edTanggal = findViewById<EditText>(R.id.edTanggal)
        val edKamar = findViewById<EditText>(R.id.edKamar)
        val edStatus = findViewById<EditText>(R.id.edStatus)
        edFoto = findViewById(R.id.edFoto)
        imvGambar = findViewById(R.id.imvGambar)

        // Get reference to the save button
        val btnSimpan = findViewById<Button>(R.id.btnSimpan1)

        // Set click listener for the save button
        btnSimpan.setOnClickListener {
            // Validate that the image is selected
            if (selectedImageUri == null) {
                Toast.makeText(this, "Please select a photo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Collect the input data
            val penyewaData = hashMapOf(
                "id_penyewa" to idPenyewa,  // Auto-generated id
                "nama" to edNama.text.toString(),
                "nik" to edNik.text.toString(),
                "alamat" to edAlamat.text.toString(),
                "telepon" to edTelepon.text.toString(),
                "email" to edEmail.text.toString(),
                "password" to edPassword.text.toString(),
                "tanggal_masuk" to edTanggal.text.toString(),
                "kamar" to edKamar.text.toString(),
                "status" to edStatus.text.toString(),
                "foto_ktp" to edFoto.text.toString() // Initialize it to empty, we will update it after upload
            )

            // Validate that all other fields are filled
            if (penyewaData.containsValue("")) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Upload image to Firebase Storage
                uploadImageToStorage(selectedImageUri!!) { downloadUrl ->
                    if (downloadUrl != null) {
                        // Update the penyewaData with the image URL
                        penyewaData["foto_ktp"] = downloadUrl

                        // Save the data to Firestore using auto-generated id_penyewa
                        // Save the data to Firestore using auto-generated id_penyewa
                        firestore.collection("penyewa").document(idPenyewa)
                            .set(penyewaData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Penyewa added successfully!", Toast.LENGTH_SHORT).show()

                                // Set the result to indicate success
                                setResult(Activity.RESULT_OK, Intent().apply {
                                    putExtra("id_penyewa", idPenyewa) // Pass the id_penyewa if needed
                                })
                                finish() // Close the activity after saving
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to add penyewa: ${e.message}", Toast.LENGTH_SHORT).show()
                            }

                    }
                }
            }
        }

        // Set click listener for the image view to open gallery
        imvGambar.setOnClickListener {
            openGallery()
        }
    }

    // Function to generate a random id_penyewa
    private fun generateRandomIdPenyewa(): String {
        val random = Random()
        val randomNumber = random.nextInt(999999) + 100000 // Generate 6-digit random number
        return "P$randomNumber" // Prefix with "P"
    }

    // Function to open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle the image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data // Save the selected image URI
            selectedImageUri?.let {
                // Display the selected image in the ImageView
                imvGambar.setImageURI(it)

                // Get the image file name and display it in the EditText
                val imageFileName = getFileNameFromUri(it)
                edFoto.setText(imageFileName)
            }
        }
    }

    // Function to get file name from Uri
    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                fileName = it.getString(nameIndex)
            }
        }
        return fileName
    }

    // Function to upload image to Firebase Storage
    private fun uploadImageToStorage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val fileName = UUID.randomUUID().toString() // Generate unique file name
        val imageRef = storageRef.child("images/$fileName") // Path to save the image

        // Upload the image
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Get the download URL
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString()) // Pass the download URL back to the caller
                }.addOnFailureListener {
                    onComplete(null) // Handle the failure case
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete(null) // Handle the failure case
            }
    }
}