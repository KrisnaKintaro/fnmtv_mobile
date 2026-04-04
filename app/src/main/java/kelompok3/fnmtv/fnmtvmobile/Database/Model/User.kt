package kelompok3.fnmtv.fnmtvmobile.Database.Model

enum class RoleUser {
    Admin, Viewer, Editor, Redaksi
}

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val role: String = RoleUser.Viewer.name
)