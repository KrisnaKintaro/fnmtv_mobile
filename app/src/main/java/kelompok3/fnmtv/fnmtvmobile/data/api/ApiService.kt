package kelompok3.fnmtv.fnmtvmobile.data.api

import kelompok3.fnmtv.fnmtvmobile.data.model.User
import kelompok3.fnmtv.fnmtvmobile.data.model.admin.UserResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.AuthResponse
import kelompok3.fnmtv.fnmtvmobile.data.model.auth.LoginRequest

import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path



interface ApiService {
    // Api get token csrf
    @GET("../sanctum/csrf-cookie")
    suspend fun getCsrfCookie(): Response<Unit>

    // Servis untuk memaksa server laravel ngirim data dengan format JSON supaya Android tidak crash kalau server ngirim error HTML
    @Headers("Accept: application/json")

    // Api viewer (home dan master)
    @GET("viewers/berita")
    suspend fun getSemuaBerita(): Response<BeritaResponse>
    @GET("viewers/kategori")
    suspend fun getKategori(): Response<KategoriResponse>

    // Api auth
    @POST("auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest // @Body artinya data dikirim sebagai raw JSON di body request
    ): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logoutUser(): Response<Unit>

    // Api manajemen user
    @GET("admin/manajemen_user/ambilData")
    suspend fun getAllUsers(): Response<UserResponse>
    @POST("admin/manajemen_user/tambahData")
    suspend fun addUser(@Body user: User): Response<Unit>
    @PUT("admin/manajemen_user/ubahData/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<Unit>
    @DELETE("admin/manajemen_user/hapusData/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Unit>
}