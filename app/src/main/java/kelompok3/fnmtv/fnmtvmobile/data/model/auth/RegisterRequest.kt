package kelompok3.fnmtv.fnmtvmobile.data.model.auth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)