package kelompok3.fnmtv.fnmtvmobile.data.model.auth

// Data yang dikirim dari HP ke Laravel
data class LoginRequest(
    val email: String,
    val password: String
)