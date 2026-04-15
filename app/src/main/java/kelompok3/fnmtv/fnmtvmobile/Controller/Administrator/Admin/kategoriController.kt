package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin

import android.content.ContentValues
import android.content.Context
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Kategori

class KategoriController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // FUNGSI READ: Ambil semua data
    // 1. UPDATE FUNGSI READ
    fun getAllKategori(): List<Kategori> {
        val kategoriList = mutableListOf<Kategori>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM kategoris ORDER BY id DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val kategori = Kategori(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nama_kategori = cursor.getString(cursor.getColumnIndexOrThrow("nama_kategori")),
                    slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")) // <-- TAMBAHAN
                )
                kategoriList.add(kategori)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return kategoriList
    }

    // 2. UPDATE FUNGSI CREATE
    fun tambahKategori(kategori: Kategori): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nama_kategori", kategori.nama_kategori)
            put("slug", kategori.slug) // <-- TAMBAHAN
        }
        val result = db.insert("kategoris", null, values)
        db.close()
        return result != -1L
    }

    // 3. UPDATE FUNGSI UPDATE (EDIT)
    fun editKategori(kategori: Kategori): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nama_kategori", kategori.nama_kategori)
            put("slug", kategori.slug) // <-- TAMBAHAN
        }
        val result = db.update("kategoris", values, "id = ?", arrayOf(kategori.id.toString()))
        db.close()
        return result > 0
    }

    // FUNGSI DELETE: Hapus data
    fun hapusKategori(kategoriId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val result = db.delete("kategoris", "id = ?", arrayOf(kategoriId.toString()))
        db.close()
        return result != -1
    }
}