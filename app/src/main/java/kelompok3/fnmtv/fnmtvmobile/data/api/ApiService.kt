package kelompok3.fnmtv.fnmtvmobile.data.api

import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.ForgotPasswordResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.LoginRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterRequest
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.RegisterResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.DetailBeritaResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.IklanResponse

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("sanctum/csrf-cookie")
    suspend fun getCsrfCookie(): Response<Unit>

    // --- Api Viewer (Tanpa Login) ---
    @GET("api/viewers/berita")
    suspend fun getSemuaBerita(): Response<BeritaResponse>

    @GET("api/viewers/kategori")
    suspend fun getKategori(): Response<KategoriResponse>

    @GET("api/viewers/kategori/{slug}")
    suspend fun getBeritaByKategori(@Path("slug") slug: String): Response<BeritaResponse>

    @GET("api/viewers/berita/{slug}")
    suspend fun getDetailBerita(@Path("slug") slug: String): Response<DetailBeritaResponse>

    @GET("api/viewers/search")
    suspend fun searchBerita(@Query("q") keyword: String): Response<BeritaResponse>

    @GET("api/viewers/iklan")
    suspend fun getIklan(): Response<IklanResponse>

    // --- Api Interaksi (Reaksi & Komentar - Butuh Login) ---
    @FormUrlEncoded
    @POST("api/viewers/toggleReaksi")
    suspend fun kirimReaksi(
        @Header("Authorization") token: String,
        @Field("berita_id") beritaId: Int,
        @Field("jenis_reaksi") jenisReaksi: String
    ): Response<Unit>

    // --- Api Auth ---
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun sendResetLink(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/auth/logout")
    suspend fun logoutUser(): Response<Unit>

    @FormUrlEncoded
    @POST("api/auth/ganti-password")
    suspend fun gantiPasswordViewer(
        @Header("Authorization") token: String,
        @Field("password_lama") passwordLama: String,
        @Field("password_baru") passwordBaru: String,
        @Field("password_baru_confirmation") konfirmasiPassword: String
    ): Response<Unit>

    // --- Api Update Profil---
    @FormUrlEncoded
    @PUT("api/viewers/update-profil")
    suspend fun updateProfilViewer(
        @Header("Authorization") token: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("current_password") currentPassword: String,
        @Field("password") newPassword: String,
        @Field("password_confirmation") konfirmasi: String
    ): Response<Unit>

    // API CRUD Komentar

    @FormUrlEncoded
    @PUT("viewers/komentar/{id}")
    suspend fun editKomentar(
        @Path("id") id: Int,
        @Field("isi_komentar") isiKomentar: String
    ): Response<Unit> // Sesuaikan dengan class Response lu

    @DELETE("viewers/komentar/{id}")
    suspend fun hapusKomentar(
        @Path("id") id: Int
    ): Response<Unit>

    // API CRUD Komentar

    @FormUrlEncoded
    @POST("api/viewers/tambahKomentar")
    suspend fun kirimKomentar(
        @Header("Authorization") token: String,
        @Field("berita_id") beritaId: Int,
        @Field("isi_komentar") isiKomentar: String
    ): Response<Unit>

    @FormUrlEncoded
    @PUT("api/viewers/komentar/{id}")
    suspend fun editKomentar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("isi_komentar") isiKomentar: String
    ): Response<Unit>

    @DELETE("api/viewers/komentar/{id}")
    suspend fun hapusKomentar(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}