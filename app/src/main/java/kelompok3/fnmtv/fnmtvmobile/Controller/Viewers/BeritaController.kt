package kelompok3.fnmtv.fnmtvmobile.Controller.Viewers

import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita

class BeritaController {
    // Fungsi untuk mengambil berita trending (view terbanyak)
    fun getTrendingBerita(): List<Berita> {
        // Logika database/API query ORDER BY jumlah_view DESC
        return emptyList()
    }

    // Fungsi untuk mencari berita berdasarkan keyword
    fun searchBerita(query: String): List<Berita> {
        // Logika database/API query WHERE judul_berita LIKE %query%
        return emptyList()
    }

    // Fungsi untuk filter berdasarkan kategori
    fun getBeritaByKategori(kategoriId: Int): List<Berita> {
        // Logika database/API query WHERE kategori_id = kategoriId
        return emptyList()
    }
}