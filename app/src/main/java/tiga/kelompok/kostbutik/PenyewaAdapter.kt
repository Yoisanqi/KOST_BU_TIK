package tiga.kelompok.kostbutik

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class PenyewaAdapter(private val context: Context, private val penyewaList: ArrayList<Penyewa>) :
    ArrayAdapter<Penyewa>(context, 0, penyewaList) {

    private val db = FirebaseFirestore.getInstance()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_penyewa, parent, false)

        val penyewa = penyewaList[position]

        // Mengisi TextView dengan data penyewa
        view.findViewById<TextView>(R.id.txNama).text = penyewa.nama
        view.findViewById<TextView>(R.id.txNik).text = penyewa.nik
        view.findViewById<TextView>(R.id.txAlamat).text = penyewa.alamat
        view.findViewById<TextView>(R.id.txTelepon).text = penyewa.telepon
        view.findViewById<TextView>(R.id.txEmail).text = penyewa.email
        view.findViewById<TextView>(R.id.txTanggal).text = penyewa.tanggal_masuk
        view.findViewById<TextView>(R.id.txKamar).text = penyewa.kamar
        view.findViewById<TextView>(R.id.txStatus).text = penyewa.status

        // Memuat gambar dari URL foto_ktp menggunakan Picasso
        val imageView = view.findViewById<ImageView>(R.id.imageView4)
        if (penyewa.foto_ktp.isNotEmpty()) {
            Picasso.get()
                .load(penyewa.foto_ktp) // URL dari Firestore
                .placeholder(R.drawable.penyewa) // Placeholder saat loading
                .error(R.drawable.delete) // Gambar jika terjadi error
                .into(imageView)
        } else {
            imageView.setImageResource(R.drawable.penyewa) // Default image
        }

        // Button untuk mengubah data penyewa
        val btnUbah3 = view.findViewById<ImageView>(R.id.btnUbah3)
        btnUbah3.setOnClickListener {
            val intent = Intent(context, UbahPenyewaActivity::class.java).apply {
                putExtra("id_penyewa", penyewa.id_penyewa) // Mengirimkan ID penyewa
            }
            (context as FragmentActivity).startActivityForResult(intent, FragmentPengelolaPenyewa.UPDATE_PENYEWA_REQUEST_CODE)
        }


        // Button untuk menghapus data penyewa
        val delete1 = view.findViewById<ImageView>(R.id.delete1)
        delete1.setOnClickListener {
            // Create an AlertDialog to confirm deletion
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle("Konfirmasi")
            alertDialog.setMessage("Apakah Anda yakin ingin menghapus data penyewa ini?")
            alertDialog.setPositiveButton("Ya") { _, _ ->
                // Delete the penyewa from Firestore
                db.collection("penyewa").document(penyewa.id_penyewa)
                    .delete()
                    .addOnSuccessListener {
                        // Successfully deleted
                        penyewaList.removeAt(position)
                        notifyDataSetChanged()  // Refresh the list
                        Toast.makeText(context, "Data penyewa berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal menghapus data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            alertDialog.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog
            }
            alertDialog.show()
        }

        return view
    }
}