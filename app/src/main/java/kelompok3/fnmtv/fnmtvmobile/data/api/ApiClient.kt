package kelompok3.fnmtv.fnmtvmobile.data.api

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kelompok3.fnmtv.fnmtvmobile.utils.Constants
import java.util.concurrent.TimeUnit

object ApiClient {

    // 1. BIKIN TOPLES KUE (Buat nyimpen Cookie dari Laravel)
    private val cookieJar = object : CookieJar {
        private val cookieStore = HashMap<String, MutableList<Cookie>>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            // Tangkap cookie yang dikirim server dan simpan di memori HP
            cookieStore[url.host] = cookies.toMutableList()
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            // Keluarkan cookie dari memori kalau mau request lagi
            return cookieStore[url.host] ?: arrayListOf()
        }
    }

    // 2. BIKIN SATPAM HEADER (Buat nyelipin token CSRF otomatis)
    private val csrfInterceptor = Interceptor { chain ->
        val request = chain.request()
        val builder = request.newBuilder()

        // Bongkar toples kue, cari yang namanya XSRF-TOKEN
        val cookies = cookieJar.loadForRequest(request.url)
        for (cookie in cookies) {
            if (cookie.name == "XSRF-TOKEN") {
                // Decode dulu tokennya sebelum ditempel ke Header!
                val decodedToken = java.net.URLDecoder.decode(cookie.value, "UTF-8")
                builder.addHeader("X-XSRF-TOKEN", decodedToken)
            }
        }

        // Kasih tau Laravel kalau kita mintanya format JSON
        builder.addHeader("Accept", "application/json")

        chain.proceed(builder.build())
    }

    fun getApiService(context: Context): ApiService {
        // 3. PASANG TOPLES & SATPAM KE MESIN OKHTTP
        val okHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)             // <-- Mesin Cookie dinyalakan
            .addInterceptor(csrfInterceptor)  // <-- Mesin Header dinyalakan
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient) // Masukin mesinnya ke Retrofit
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}