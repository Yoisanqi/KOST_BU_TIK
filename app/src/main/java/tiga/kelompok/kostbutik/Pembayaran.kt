package tiga.kelompok.kostbutik

data class Pembayaran(
    val id: String, // ID unik untuk pembayaran
    val penyewa: String, // Nama penyewa
    val jumlahBayar: Double, // Jumlah yang dibayar
    val tanggalBayar: String, // Tanggal pembayaran
    val metodeBayar: String, // Metode pembayaran
    val statusBayar: String, // Status pembayaran (lunas/belum lunas)
    val jatuhTempo: String // Jatuh tempo pembayaran
)
