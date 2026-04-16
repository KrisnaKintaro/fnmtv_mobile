package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Editor

import android.content.ContentValues
import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita

class EditorController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // 1. Simpan Berita sebagai Draft
    fun simpanSebagaiDraft(berita: Berita): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("user_id", berita.user_id)
            put("kategori_id", berita.kategori_id)
            put("judul_berita", berita.judul_berita)
            put("slug", berita.judul_berita.lowercase().replace(" ", "-"))
            put("isi_berita", berita.isi_berita)
            put("foto_thumbnail", berita.foto_thumbnail)
            put("status_berita", "Draft")
        }
        val result = db.insert("beritas", null, values)
        db.close()
        return result != -1L
    }

    // 2. Ajukan Publikasi (Status -> Pending)
    fun ajukanPublikasi(beritaId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("status_berita", "Pending")
        }
        val result = db.update("beritas", values, "id = ?", arrayOf(beritaId.toString()))
        db.close()
        return result > 0
    }

    // 3. Ambil daftar berita milik editor yang sedang login
    fun getBeritaSaya(userId: Int): List<Berita> {
        val listBerita = mutableListOf<Berita>()
        val db = dbHelper.readableDatabase
        val query = """
            SELECT b.*, k.nama_kategori 
            FROM beritas b 
            LEFT JOIN kategoris k ON b.kategori_id = k.id 
            WHERE b.user_id = ? AND b.deleted_at IS NULL
            ORDER BY b.id DESC
        """.trimIndent()
        
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                listBerita.add(Berita(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    user_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                    kategori_id = cursor.getInt(cursor.getColumnIndexOrThrow("kategori_id")),
                    judul_berita = cursor.getString(cursor.getColumnIndexOrThrow("judul_berita")),
                    slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
                    isi_berita = cursor.getString(cursor.getColumnIndexOrThrow("isi_berita")),
                    foto_thumbnail = cursor.getString(cursor.getColumnIndexOrThrow("foto_thumbnail")),
                    catatan_penolakan = cursor.getString(cursor.getColumnIndexOrThrow("catatan_penolakan")),
                    status_berita = cursor.getString(cursor.getColumnIndexOrThrow("status_berita")),
                    nama_kategori = cursor.getString(cursor.getColumnIndexOrThrow("nama_kategori")),
                    created_at = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listBerita
    }

    // 4. Revisi berita yang statusnya 'Rejected'
    fun revisiBerita(id: Int, judulBaru: String, kontenBaru: String, fotoBaru: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("judul_berita", judulBaru)
            put("isi_berita", kontenBaru)
            put("foto_thumbnail", fotoBaru)
            put("status_berita", "Draft") // Kembali ke draft setelah revisi agar bisa diajukan ulang
        }
        val result = db.update("beritas", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return result > 0
    }
}
