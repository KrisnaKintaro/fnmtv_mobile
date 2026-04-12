package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class ViewLog(
    val id: Int = 0,
    val beritaId: Int,
    val ipAddress: String? = null,
    val nama_user: String? = null,
    val nama_berita: String? = null,
    val created_at: String? = null
)