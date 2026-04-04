package kelompok3.fnmtv.fnmtvmobile.Database.Model

enum class StatusPembayaran {
    Paid, Unpaid
}

data class Pendapatan(
    val id: Int = 0,
    val beritaId: Int,
    val userId: Int,
    val nominalPendapatan: Double = 0.0,
    val statusPembayaran: String = StatusPembayaran.Unpaid.name,
    val waktuPembayaran: String? = null,

    // Hasil Query JOIN untuk UI
    val judulBerita: String = "",
    val namaPenulis: String = ""
)