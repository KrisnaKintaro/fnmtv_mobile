package kelompok3.fnmtv.fnmtvmobile.data.model

data class User(
    val id: Int? = null,
    val username: String?,
    val email: String?,
    val role: String?,
    val status: String? = "Aktif",
    val password: String? = null
)