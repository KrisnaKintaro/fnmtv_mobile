package kelompok3.fnmtv.fnmtvmobile.data.model.viewer

import com.google.gson.annotations.SerializedName

data class BeritaResponse(
    val status: String? = null,
    val data: BeritaDataGroup? = null
)

data class BeritaDataGroup(
    val headline: List<BeritaItem>? = null,
    val terbaru: List<BeritaItem>? = null,
    val trending: List<BeritaItem>? = null
)

data class BeritaItem(
    val id: Int? = null,
    @SerializedName("judul_berita") val judulBerita: String? = null,
    val slug: String? = null,
    @SerializedName("foto_thumbnail") val fotoThumbnail: String? = null,
    @SerializedName("waktu_publikasi") val waktuPublikasi: String? = null,
    @SerializedName("jumlah_view") val jumlahView: String? = null,
    @SerializedName("isi_berita") val isiBerita: String? = null,
    val kategori: KategoriBerita? = null,
    val user: UserBerita? = null,

    // FIX: key JSON backend adalah "reaksi_rekap", bukan "reaksi_count"
    @SerializedName("reaksi_rekap") val reaksiRekap: ReaksiRekap? = null,

    // FIX: key JSON backend adalah "komentar", bukan "komentars"
    @SerializedName("komentar") val komentar: List<KomentarItem>? = null
)

// FIX: model baru sesuai struktur JSON backend
// {"suka":0,"cinta":0,"kaget":0,"sedih":0,"marah":0}
data class ReaksiRekap(
    val suka: Int? = 0,
    val cinta: Int? = 0,
    val kaget: Int? = 0,
    val sedih: Int? = 0,
    val marah: Int? = 0
)

data class KomentarItem(
    val id: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("isi_komentar") val isiKomentar: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    val user: UserBerita? = null
)

data class KategoriBerita(
    @SerializedName("nama_kategori") val namaKategori: String? = null
)

data class UserBerita(
    val username: String? = null
)