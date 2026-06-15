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
    @SerializedName("isi_berita") val isiBerita: String?,

    // Metadata Tambahan
    val kategori: KategoriBerita?,
    val user: UserBerita?,
    
    // Relationship Reaksi & Komentar (Disesuaikan dengan Laravel)
    @SerializedName("reaksi_count") val reaksiCount: Map<String, Int>? = null,
    val komentars: List<KomentarItem>? = null
)

data class KomentarItem(
    val id: Int?,
    @SerializedName("isi_komentar") val isiKomentar: String?,
    @SerializedName("created_at") val createdAt: String?,
    val user: UserBerita?
)

data class KategoriBerita(
    @SerializedName("nama_kategori") val namaKategori: String?
)

data class UserBerita(
    val username: String?
)
