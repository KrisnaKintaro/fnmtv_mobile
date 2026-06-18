package kelompok3.fnmtv.fnmtvmobile.data.api

import kelompok3.fnmtv.fnmtvmobile.data.model.User
import kelompok3.fnmtv.fnmtvmobile.data.model.admin.UserResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.LoginRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.DetailBeritaResponse

import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("../sanctum/csrf-cookie")
    suspend fun getCsrfCookie(): Response<Unit>

    @Headers("Accept: application/json")

    // --- Api Viewer ---
    @GET("viewers/berita")
    suspend fun getSemuaBerita(): Response<BeritaResponse>

    @GET("viewers/kategori")
    suspend fun getKategori(): Response<KategoriResponse>

    @GET("viewers/berita/{slug}")
    suspend fun getDetailBerita(@Path("slug") slug: String): Response<DetailBeritaResponse>

    @GET("viewers/berita/search")
    suspend fun searchBerita(@Query("keyword") keyword: String): Response<BeritaResponse>

    @GET("viewers/berita")
    suspend fun getBeritaByKategori(@Query("kategori_id") kategoriId: Int): Response<BeritaResponse>

    // --- Api Interaksi (Reaksi & Komentar) ---
    @FormUrlEncoded
    @POST("viewers/toggleReaksi")
    suspend fun kirimReaksi(
        @Header("Authorization") token: String,
        @Field("berita_id") beritaId: Int,
        @Field("jenis_reaksi") jenisReaksi: String
    ): Response<Unit>

    @FormUrlEncoded
    @POST("viewers/tambahKomentar")
    suspend fun kirimKomentar(
        @Header("Authorization") token: String,
        @Field("berita_id") beritaId: Int,
        @Field("isi_komentar") isiKomentar: String
    ): Response<Unit>

    // --- Api Auth ---
    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/forgot-password")
    suspend fun sendResetLink(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("auth/logout")
    suspend fun logoutUser(): Response<Unit>

    // --- Api Manajemen User (Admin) ---
    @GET("admin/manajemen_user/ambilData")
    suspend fun getAllUsers(): Response<UserResponse>

    @POST("admin/manajemen_user/tambahData")
    suspend fun addUser(@Body user: User): Response<Unit>

    @PUT("admin/manajemen_user/ubahData/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<Unit>

    @DELETE("admin/manajemen_user/hapusData/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>

    // --- Api Update Profil & Password Terpadu (Viewer) ---
    @FormUrlEncoded
    @PUT("viewers/update-profil")
    suspend fun updateProfilViewer(
        @Header("Authorization") token: String,
        @Field("nama pengguna") username: String,
        @Field("email") email: String,
        @Field("kata_sandi_saat_ini") currentPassword: String,
        @Field("kata sandi") passwordBaru: String
    ): Response<Unit>

    @FormUrlEncoded
    @POST("auth/ganti-password")
    suspend fun gantiPasswordViewer(
        @Header("Authorization") token: String,
        @Field("password_lama") passwordLama: String,
        @Field("password_baru") passwordBaru: String,
        @Field("password_baru_confirmation") konfirmasiPassword: String // Sesuaikan dengan key parameter di AuthController Laravel
    ): Response<Unit>
}