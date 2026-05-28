package kelompok3.fnmtv.fnmtvmobile.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // Membuka file rahasia di dalam OS Android bernama "FNMTV_PREFS"
    private var prefs: SharedPreferences = context.getSharedPreferences("FNMTV_PREFS", Context.MODE_PRIVATE)

    // Fungsi buat nyimpen data pas berhasil login
    fun saveSession(token: String, username: String, email: String) {
        val editor = prefs.edit()
        editor.putString("USER_TOKEN", token)
        editor.putString("USER_NAME", username)
        editor.putString("USER_EMAIL", email)
        editor.apply() // Commit simpan ke memori internal
    }

    // Fungsi ngambil token buat dicek di Navbar
    fun fetchAuthToken(): String? = prefs.getString("USER_TOKEN", null)
    fun fetchUsername(): String? = prefs.getString("USER_NAME", null)

    // Fungsi hapus data pas user mencet tombol "Logout"
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}