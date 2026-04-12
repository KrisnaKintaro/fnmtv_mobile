package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class Reaksi(
    val id: Int = 0,
    val beritaId: Int,
    val userId: Int,
    val jenisReaksi: String
)