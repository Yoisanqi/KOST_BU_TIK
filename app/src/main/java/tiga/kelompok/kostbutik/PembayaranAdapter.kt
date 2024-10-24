package tiga.kelompok.kostbutik

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PembayaranAdapter(private val pembayaranList: List<Pembayaran>) :
    RecyclerView.Adapter<PembayaranAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pengelola_pembayaran, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pembayaran = pembayaranList[position]
        holder.bind(pembayaran)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pembayaran: Pembayaran) {
            // Mengatur tampilan dengan data pembayaran
            itemView.findViewById<TextView>(R.id.txPenyewa2).text = pembayaran.penyewa
            itemView.findViewById<TextView>(R.id.txJmlBayar2).text = pembayaran.jumlahBayar.toString()
            itemView.findViewById<TextView>(R.id.txTglBayar2).text = pembayaran.tanggalBayar
            itemView.findViewById<TextView>(R.id.txMetodeBayar2).text = pembayaran.metodeBayar
            itemView.findViewById<TextView>(R.id.txStatus2).text = pembayaran.statusBayar
            itemView.findViewById<TextView>(R.id.txJatuhTempo2).text = pembayaran.jatuhTempo

            itemView.findViewById<ImageView>(R.id.btnUbah4).setOnClickListener {
                // Navigasi ke UbahPembayaranActivity
                val intent = Intent(itemView.context, UbahPembayaranActivity::class.java)
                intent.putExtra("PENGGUNA_ID", pembayaran.id)
                itemView.context.startActivity(intent)
            }

            itemView.findViewById<ImageView>(R.id.btnHapus2).setOnClickListener {
                // Logic untuk menghapus pembayaran
            }
        }
    }

    override fun getItemCount() = pembayaranList.size
}
