package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Redaksi

import android.content.ContentValues
import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita

class VerifikasiBeritaController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // ambil daftar berita dgn status pending dan rejected
    fun getBeritaForRedaksi(): List<Berita> {
        val beritaList = mutableListOf<Berita>()
        val db = dbHelper.readableDatabase
        
        // Query digabung buat mendapatkan nama penulis & kategori
        val query = """
            SELECT b.*, u.username as nama_penulis, k.nama_kategori 
            FROM beritas b
            LEFT JOIN users u ON b.user_id = u.id
            LEFT JOIN kategoris k ON b.kategori_id = k.id
            WHERE b.status_berita IN ('Pending', 'Rejected') AND b.deleted_at IS NULL
            ORDER BY b.created_at DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val berita = Berita(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    kategori_id = cursor.getInt(cursor.getColumnIndexOrThrow("kategori_id")),
                    judul_berita = cursor.getString(cursor.getColumnIndexOrThrow("judul_berita")),
                    slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
                    isi_berita = cursor.getString(cursor.getColumnIndexOrThrow("isi_berita")),
                    foto_thumbnail = cursor.getString(cursor.getColumnIndexOrThrow("foto_thumbnail")),
                    foto_isi_berita = cursor.getString(cursor.getColumnIndexOrThrow("foto_isi_berita")),
                    catatan_penolakan = cursor.getString(cursor.getColumnIndexOrThrow("catatan_penolakan")),
                    status_berita = cursor.getString(cursor.getColumnIndexOrThrow("status_berita")),
                    jumlah_view = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah_view")),
                    waktu_publikasi = cursor.getString(cursor.getColumnIndexOrThrow("waktu_publikasi")),
                    nama_penulis = cursor.getString(cursor.getColumnIndexOrThrow("nama_penulis")),
                    nama_kategori = cursor.getString(cursor.getColumnIndexOrThrow("nama_kategori")),
                    created_at = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                )
                beritaList.add(berita)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return beritaList
    }

    // ambil berita yg sdh terbit
    fun getBeritaTerbit(): List<Berita> {
        val beritaList = mutableListOf<Berita>()
        val db = dbHelper.readableDatabase
        
        val query = """
            SELECT b.*, u.username as nama_penulis, k.nama_kategori 
            FROM beritas b
            LEFT JOIN users u ON b.user_id = u.id
            LEFT JOIN kategoris k ON b.kategori_id = k.id
            WHERE b.status_berita = 'Published' AND b.deleted_at IS NULL
            ORDER BY b.waktu_publikasi DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val berita = Berita(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    kategori_id = cursor.getInt(cursor.getColumnIndexOrThrow("kategori_id")),
                    judul_berita = cursor.getString(cursor.getColumnIndexOrThrow("judul_berita")),
                    slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
                    isi_berita = cursor.getString(cursor.getColumnIndexOrThrow("isi_berita")),
                    foto_thumbnail = cursor.getString(cursor.getColumnIndexOrThrow("foto_thumbnail")),
                    foto_isi_berita = cursor.getString(cursor.getColumnIndexOrThrow("foto_isi_berita")),
                    catatan_penolakan = cursor.getString(cursor.getColumnIndexOrThrow("catatan_penolakan")),
                    status_berita = cursor.getString(cursor.getColumnIndexOrThrow("status_berita")),
                    jumlah_view = cursor.getInt(cursor.getColumnIndexOrThrow("jumlah_view")),
                    waktu_publikasi = cursor.getString(cursor.getColumnIndexOrThrow("waktu_publikasi")),
                    nama_penulis = cursor.getString(cursor.getColumnIndexOrThrow("nama_penulis")),
                    nama_kategori = cursor.getString(cursor.getColumnIndexOrThrow("nama_kategori")),
                    created_at = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                )
                beritaList.add(berita)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return beritaList
    }

    //ubah status berita published atau Rejected, klo ditolak akan muncul pop up isi alasan
    fun updateStatusBerita(id: Int, status: String, catatan: String? = null): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status_berita", status)
            put("catatan_penolakan", catatan)
            if (status == "Published") {
                put("waktu_publikasi", System.currentTimeMillis().toString())
            }
        }
        val result = db.update("beritas", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
