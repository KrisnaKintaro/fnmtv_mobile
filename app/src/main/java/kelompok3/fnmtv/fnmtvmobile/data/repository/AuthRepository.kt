package kelompok3.fnmtv.fnmtvmobile.data.repository

import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.LoginRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import retrofit2.Response

class AuthRepository(private val context: Context) {
    suspend fun login(email: String, pas: String): Response<AuthResponse> {
        val bungkusanBody = LoginRequest(email, pas)
        return ApiClient.getApiService(context).loginUser(bungkusanBody)
    }
}