package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class Komentar(
    val id: Int = 0,
    val berita_id: Int,
    val user_id: Int,
    val isi_komentar: String,
    val status_moderasi: String = "Pending",

    // --- TAMBAHAN UNTUK RELASI JOIN ---
    val nama_user: String? = null,       // Buat nampilin siapa yang komen
    val judul_berita: String? = null,     // Buat nampilin komennya di artikel mana
    val created_at: String? = null,
    val deleted_at: String? = null
)