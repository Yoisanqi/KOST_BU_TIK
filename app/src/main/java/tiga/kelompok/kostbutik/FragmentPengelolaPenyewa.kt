package tiga.kelompok.kostbutik

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FragmentPengelolaPenyewa : Fragment() {

    private lateinit var listView: ListView
    private lateinit var penyewaAdapter: PenyewaAdapter
    private lateinit var penyewaList: ArrayList<Penyewa>
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pengelola_penyewa, container, false)

        // Initialize ListView and Firestore
        listView = view.findViewById(R.id.lvPenyewa)
        penyewaList = ArrayList()
        penyewaAdapter = PenyewaAdapter(requireContext(), penyewaList)
        listView.adapter = penyewaAdapter
        db = FirebaseFirestore.getInstance()

        // Fetch data from Firestore
        fetchPenyewaData()

        // Button to add tenant
        val btnTambahPenyewa = view.findViewById<Button>(R.id.btnTambahPenyewa)
        btnTambahPenyewa.setOnClickListener {
            val intent = Intent(activity, TambahPenyewaActivity::class.java)
            startActivityForResult(intent, ADD_PENYEWA_REQUEST_CODE) // Start activity for result
        }

        return view
    }

    // Function to fetch data from Firestore
    private fun fetchPenyewaData() {
        db.collection("penyewa")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    penyewaList.clear() // Clear the list before adding new data
                    for (document in snapshots) {
                        val penyewa = document.toObject(Penyewa::class.java)
                        penyewaList.add(penyewa)
                    }
                    penyewaAdapter.notifyDataSetChanged()  // Refresh ListView after data is fetched
                }
            }
    }



    // Handle results from other activities
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            when (requestCode) {
                ADD_PENYEWA_REQUEST_CODE -> {
                    fetchPenyewaData()
                    Toast.makeText(context, "Tenant added", Toast.LENGTH_SHORT).show()
                }
                UPDATE_PENYEWA_REQUEST_CODE -> {
                    fetchPenyewaData()
                    Toast.makeText(context, "Tenant updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    companion object {
        private const val ADD_PENYEWA_REQUEST_CODE = 1 // Request code for adding a tenant
        const val UPDATE_PENYEWA_REQUEST_CODE = 2  // Request code for updating a tenant
    }
}