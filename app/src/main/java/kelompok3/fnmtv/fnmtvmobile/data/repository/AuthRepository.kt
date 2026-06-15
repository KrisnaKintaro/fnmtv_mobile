package kelompok3.fnmtv.fnmtvmobile.data.repository

import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.LoginRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterResponse
import retrofit2.Response

class AuthRepository(private val context: Context) {
    suspend fun login(email: String, pas: String): Response<AuthResponse> {
        val bungkusanBody = LoginRequest(email, pas)
        return ApiClient.getApiService(context).loginUser(bungkusanBody)
    }

    suspend fun register(username: String, email: String, pas: String, pasConf: String): Response<RegisterResponse> {
        val bungkusanBody = RegisterRequest(username, email, pas, pasConf)
        return ApiClient.getApiService(context).registerUser(bungkusanBody)
    }

    suspend fun sendResetPasswordLink(email: String): Response<ForgotPasswordResponse> {
        val bungkusanBody = ForgotPasswordRequest(email)
        return ApiClient.getApiService(context).sendResetLink(bungkusanBody)
    }
}