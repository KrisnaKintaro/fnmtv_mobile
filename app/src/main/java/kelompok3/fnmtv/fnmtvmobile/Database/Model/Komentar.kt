package kelompok3.fnmtv.fnmtvmobile.Database.Model

enum class StatusModerasi {
    Pending, Approved, Spam
}

data class Komentar(
    val id: Int = 0,
    val beritaId: Int,
    val userId: Int,
    val isiKomentar: String,
    val statusModerasi: String = StatusModerasi.Pending.name,

    // Hasil Query JOIN untuk UI
    val namaPengomentar: String = ""
)