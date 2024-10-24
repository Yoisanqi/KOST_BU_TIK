package tiga.kelompok.kostbutik

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FragmentPengelolaPembayaran : Fragment() {

    private lateinit var btnTambahPembayaran: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var pembayaranList: List<Pembayaran>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pengelola_pembayaran, container, false)
        btnTambahPembayaran = view.findViewById(R.id.btnTambahPembayaran)
        recyclerView = view.findViewById(R.id.lvPembayaran)

        btnTambahPembayaran.setOnClickListener {
            // Navigasi ke TambahPembayaranActivity
            val intent = Intent(activity, TambahPembayaranActivity::class.java)
            startActivity(intent)
        }

        // Inisialisasi RecyclerView dengan adapter
        pembayaranList = listOf() // Ambil data pembayaran dari sumber data
        val adapter = PembayaranAdapter(pembayaranList)
        recyclerView.adapter = adapter

        return view
    }
}

