package kelompok3.fnmtv.fnmtvmobile.Database.Model

data class `Kategori.kt`(
    val id: Int = 0,
    val namaKategori: String,
    val slug: String
) {
    override fun toString(): String = namaKategori
}