package kelompok3.fnmtv.fnmtvmobile.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("FNMTV_PREFS", Context.MODE_PRIVATE)

    fun saveSession(token: String, userId: Int, username: String, email: String) {
        val editor = prefs.edit()
        editor.putString("USER_TOKEN", token)
        editor.putInt("USER_ID", userId) // Simpan ID
        editor.putString("USER_NAME", username)
        editor.putString("USER_EMAIL", email)
        editor.apply()
    }

    fun fetchUserId(): Int? {
        val id = prefs.getInt("USER_ID", -1)
        return if (id != -1) id else null
    }

    fun fetchAuthToken(): String? = prefs.getString("USER_TOKEN", null)
    fun fetchUsername(): String? = prefs.getString("USER_NAME", null)
    fun fetchEmail(): String? = prefs.getString("USER_EMAIL", null)

    fun saveUsername(username: String) {
        prefs.edit().putString("USER_NAME", username).apply()
    }

    fun saveEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}