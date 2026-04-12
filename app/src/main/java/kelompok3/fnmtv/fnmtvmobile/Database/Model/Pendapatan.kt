package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class Pendapatan(
    val id: Int = 0,
    val berita_id: Int,
    val user_id: Int,
    val nominal_pendapatan: Double = 0.0,
    val status_pembayaran: String = "Unpaid",
    val waktu_pembayaran: String? = null,

    val judul_berita: String? = null,
    val created_at: String? = null,
    val deleted_at: String? = null
)