package kelompok3.fnmtv.fnmtvmobile.data.api.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriBerita
import kelompok3.fnmtv.fnmtvmobile.utils.Constants

class VolleyManager(context: Context) {
    // Siapin antrean Volley
    private val requestQueue = Volley.newRequestQueue(context)

    fun searchBerita(keyword: String, onSuccess: (List<BeritaItem>) -> Unit, onError: (String) -> Unit) {
        val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
        val url = "${Constants.BASE_URL}viewers/search?q=$encodedKeyword"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        // Ambil Object Pagination-nya dulu
                        val paginationObject = response.getJSONObject("data")
                        // Baru ambil Array aslinya dari dalam Object Pagination tadi
                        val dataArray = paginationObject.getJSONArray("data")

                        val listHasil = mutableListOf<BeritaItem>()

                        // Bongkar JSON Array jadi List<BeritaItem>
                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)

                            // Tarik Kategori (Kalau ada)
                            val kategoriObj = if (obj.has("kategori") && !obj.isNull("kategori")) obj.getJSONObject("kategori") else null
                            val namaKategori = kategoriObj?.optString("nama_kategori", "UMUM") ?: "UMUM"

                            // Cetak jadi Objek Kotlin
                            val berita = BeritaItem(
                                id = obj.optInt("id"),
                                judulBerita = obj.optString("judul_berita"),
                                slug = obj.optString("slug"),
                                fotoThumbnail = obj.optString("foto_thumbnail"),
                                waktuPublikasi = obj.optString("waktu_publikasi"),
                                jumlahView = obj.optString("jumlah_view"),
                                isiBerita = obj.optString("isi_berita"), // ← TAMBAH INI
                                kategori = KategoriBerita(namaKategori),
                                user = null
                            )
                            listHasil.add(berita)
                        }
                        onSuccess(listHasil) // Lempar data yang udah mateng ke UI!
                    } else {
                        onError("Data tidak ditemukan.")
                    }
                } catch (e: Exception) {
                    onError("Gagal memproses data JSON: ${e.message}")
                }
            },
            { error ->
                onError("Koneksi gagal! Cek internet lu. (${error.message})")
            }
        )
        requestQueue.add(request)
    }
}