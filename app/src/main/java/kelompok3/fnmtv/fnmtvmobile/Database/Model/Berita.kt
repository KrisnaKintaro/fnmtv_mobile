package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class Berita(
    val id: Int = 0,
    val user_id: Int,
    val kategori_id: Int,
    val judul_berita: String,
    val slug: String,
    val isi_berita: String,
    val foto_thumbnail: String,
    val foto_isi_berita: String? = null,
    val catatan_penolakan: String? = null,
    val status_berita: String = "Draft",
    val jumlah_view: Int = 0,
    val waktu_publikasi: String? = null,

    // --- TAMBAHAN UNTUK RELASI (Hasil JOIN) ---
    // Properti ini kita set nullable (?) biar gak error kalau kita cuma query tabel beritas doang
    val nama_penulis: String? = null,
    val nama_kategori: String? = null,
    val created_at: String? = null,
    val deleted_at: String? = null
)