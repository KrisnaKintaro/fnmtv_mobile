package kelompok3.fnmtv.fnmtvmobile.data.api.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriBerita
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.UserBerita
import kelompok3.fnmtv.fnmtvmobile.utils.Constants

class VolleyManager(context: Context) {
    private val requestQueue = Volley.newRequestQueue(context)

    fun searchBerita(keyword: String, onSuccess: (List<BeritaItem>) -> Unit, onError: (String) -> Unit) {
        val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
        val url = "${Constants.BASE_URL}api/viewers/search?q=$encodedKeyword"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    if (response.getString("status") == "success") {
                        val paginationObject = response.getJSONObject("data")
                        val dataArray = paginationObject.getJSONArray("data")

                        val listHasil = mutableListOf<BeritaItem>()

                        for (i in 0 until dataArray.length()) {
                            val obj = dataArray.getJSONObject(i)

                            // Parsing Kategori
                            val kategori = if (obj.has("kategori") && !obj.isNull("kategori")) {
                                val kat = obj.getJSONObject("kategori")
                                KategoriBerita(namaKategori = kat.optString("nama_kategori", "UMUM"))
                            } else null

                            // Parsing User (biar sinkron sama SearchFragment lu)
                            val user = if (obj.has("user") && !obj.isNull("user")) {
                                val usr = obj.getJSONObject("user")
                                UserBerita(username = usr.optString("username", ""))
                            } else null

                            val berita = BeritaItem(
                                id = obj.optInt("id"),
                                judulBerita = obj.optString("judul_berita"),
                                slug = obj.optString("slug"),
                                fotoThumbnail = obj.optString("foto_thumbnail"),
                                waktuPublikasi = obj.optString("waktu_klik", obj.optString("waktu_publikasi")),
                                jumlahView = obj.optString("jumlah_view"),
                                isiBerita = obj.optString("isi_berita"),
                                kategori = kategori,
                                user = user
                            )
                            listHasil.add(berita)
                        }
                        onSuccess(listHasil)
                    } else {
                        onError("Data tidak ditemukan.")
                    }
                } catch (e: Exception) {
                    onError("Gagal memproses data JSON: ${e.message}")
                }
            },
            { error ->
                onError("Koneksi gagal! Cek internet lu. (${error.message ?: "Unknown Error"})")
            }
        )
        requestQueue.add(request)
    }
}