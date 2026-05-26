package kelompok3.fnmtv.fnmtvmobile.data.model.viewer

import com.google.gson.annotations.SerializedName

data class KategoriResponse(
    val status: String,
    val message: String?,
    val data: List<KategoriItem>
)

data class KategoriItem(
    val id: Int,
    @SerializedName("nama_kategori") val namaKategori: String,
    val slug: String
)