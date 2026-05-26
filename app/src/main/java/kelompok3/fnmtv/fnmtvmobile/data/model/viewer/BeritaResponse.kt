package kelompok3.fnmtv.fnmtvmobile.data.model.viewer

import com.google.gson.annotations.SerializedName

data class BeritaResponse(
    val status: String?,
    val data: BeritaDataGroup?
)

data class BeritaDataGroup(
    val headline: List<BeritaItem>?,
    val terbaru: List<BeritaItem>?,
    val trending: List<BeritaItem>?
)

data class BeritaItem(
    val id: Int?,
    @SerializedName("judul_berita") val judulBerita: String?,
    val slug: String?,
    @SerializedName("foto_thumbnail") val fotoThumbnail: String?,
    @SerializedName("waktu_publikasi") val waktuPublikasi: String?,
    @SerializedName("jumlah_view") val jumlahView: String?,

    val kategori: KategoriBerita?,
    val user: UserBerita?
)

data class KategoriBerita(
    @SerializedName("nama_kategori") val namaKategori: String?
)

data class UserBerita(
    val username: String?
)