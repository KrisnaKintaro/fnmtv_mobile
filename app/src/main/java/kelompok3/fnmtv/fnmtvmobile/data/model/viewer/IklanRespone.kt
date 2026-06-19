package kelompok3.fnmtv.fnmtvmobile.data.model.viewer

import com.google.gson.annotations.SerializedName

data class IklanResponse(
    val status: String?,
    val data: IklanDataLayout? // Diubah menjadi Object, bukan List langsung
)

data class IklanDataLayout(
    @SerializedName("horizontal_728x90") val horizontalIklan: List<IklanItem>?,
    @SerializedName("sidebar_300x250") val sidebarIklan: List<IklanItem>?
)

data class IklanItem(
    val id: Int?,
    val judul: String?, // Sesuaikan dengan field 'judul' di Laravel
    val gambar: String?,
    @SerializedName("link_tujuan") val linkIklan: String?,
    val posisi: String?
)