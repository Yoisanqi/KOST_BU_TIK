package tiga.kelompok.kostbutik

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class PembayaranActivity : AppCompatActivity() {

    private lateinit var btnUbah2: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran)

        btnUbah2 = findViewById(R.id.btnUbah2)

        btnUbah2.setOnClickListener {
            // Logic untuk mengubah pembayaran
            val intent = Intent(this, UbahPembayaranActivity::class.java)
            startActivity(intent)
        }
    }
}


