package kelompok3.fnmtv.fnmtvmobile.Database.Model

enum class StatusBerita {
    Draft, Pending, Published, Rejected
}

data class Berita(
    val id: Int = 0,
    val userId: Int,
    val kategoriId: Int,
    val judulBerita: String,
    val slug: String,
    val isiBerita: String,
    val fotoThumbnail: String,
    val fotoIsiBerita: String? = null,
    val catatanPenolakan: String? = null,
    val statusBerita: String = StatusBerita.Draft.name,
    val jumlahView: Int = 0,
    val waktuPublikasi: String? = null,

    // Hasil Query JOIN untuk UI
    val namaPenulis: String = "",
    val namaKategori: String = ""
)