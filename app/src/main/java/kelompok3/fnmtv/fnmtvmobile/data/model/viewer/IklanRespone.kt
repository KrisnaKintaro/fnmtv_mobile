// IklanRespones
package kelompok3.fnmtv.fnmtvmobile.data.model.viewer
import com.google.gson.annotations.SerializedName

data class IklanResponse(
    val status: String?,
    val data: List<IklanItem>?
)

data class IklanItem(
    val id: Int?,
    @SerializedName("judul_iklan") val judulIklan: String?,
    @SerializedName("gambar_iklan") val gambarIklan: String?,
    @SerializedName("link_iklan") val linkIklan: String?,
    val posisi: String?
)