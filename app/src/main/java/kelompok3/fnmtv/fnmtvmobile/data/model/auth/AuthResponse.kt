package kelompok3.fnmtv.fnmtvmobile.data.model.auth
import kelompok3.fnmtv.fnmtvmobile.data.model.User

// Data yang diterima HP dari Laravel pas sukses login
data class AuthResponse(
    val status: String?,
    val message: String?,
    val token: String?, // Token Sanctum (Bearer Token) buat gembok keamanan API
    val redirect: String?,
    val data: User?,    // Memasukkan cetakan model User di atas
    val user: User?
)