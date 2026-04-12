package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class Kategori(
    val id: Int = 0,
    val nama_kategori: String,
    val slug: String,

    val jumlah_berita: Int? = 0,
    val created_at: String? = null
)