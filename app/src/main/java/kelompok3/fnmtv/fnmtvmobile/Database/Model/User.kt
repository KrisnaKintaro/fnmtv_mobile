package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val role: String,
    val status: String = "Aktif",
    val password: String = "",
    val created_at: String? = null
)