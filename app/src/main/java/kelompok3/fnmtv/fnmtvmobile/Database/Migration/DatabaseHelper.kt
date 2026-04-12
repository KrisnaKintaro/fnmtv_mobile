package kelompok3.fnmtv.fnmtvmobile.Database.Migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "FnmtvMobileDB", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // 1. Table Users
        db.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT CHECK(role IN ('Admin', 'Viewer', 'Editor', 'Redaksi')) DEFAULT 'Viewer',
                status TEXT CHECK(status IN ('Aktif', 'Nonaktif')) DEFAULT 'Aktif'
            )
        """.trimIndent())

        // 2. Table Kategoris
        db.execSQL("""
            CREATE TABLE kategoris (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama_kategori TEXT NOT NULL,
                slug TEXT UNIQUE NOT NULL
            )
        """.trimIndent())

        // 3. Table Beritas
        db.execSQL("""
            CREATE TABLE beritas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                kategori_id INTEGER,
                judul_berita TEXT NOT NULL,
                slug TEXT UNIQUE,
                isi_berita TEXT NOT NULL,
                foto_thumbnail TEXT NOT NULL,
                foto_isi_berita TEXT,
                catatan_penolakan TEXT,
                status_berita TEXT CHECK(status_berita IN ('Draft', 'Pending', 'Published', 'Rejected')) DEFAULT 'Draft',
                jumlah_view INTEGER DEFAULT 0,
                waktu_publikasi TEXT,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(kategori_id) REFERENCES kategoris(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 4. Table Pendapatans
        db.execSQL("""
            CREATE TABLE pendapatans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                berita_id INTEGER,
                user_id INTEGER,
                nominal_pendapatan REAL DEFAULT 0,
                status_pembayaran TEXT CHECK(status_pembayaran IN ('Paid', 'Unpaid')) DEFAULT 'Unpaid',
                waktu_pembayaran TEXT,
                FOREIGN KEY(berita_id) REFERENCES beritas(id) ON DELETE CASCADE,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 5. Table Komentars
        db.execSQL("""
            CREATE TABLE komentars (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                berita_id INTEGER,
                user_id INTEGER,
                isi_komentar TEXT NOT NULL,
                status_moderasi TEXT CHECK(status_moderasi IN ('Pending', 'Approved', 'Spam')) DEFAULT 'Pending',
                FOREIGN KEY(berita_id) REFERENCES beritas(id) ON DELETE CASCADE,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 6. Table Reaksis
        db.execSQL("""
            CREATE TABLE reaksis (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                berita_id INTEGER,
                user_id INTEGER,
                jenis_reaksi TEXT NOT NULL,
                FOREIGN KEY(berita_id) REFERENCES beritas(id) ON DELETE CASCADE,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 7. Table ViewLogs
        db.execSQL("""
            CREATE TABLE view_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                berita_id INTEGER,
                ip_address TEXT,
                FOREIGN KEY(berita_id) REFERENCES beritas(id) ON DELETE CASCADE
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS view_logs")
        db.execSQL("DROP TABLE IF EXISTS reaksis")
        db.execSQL("DROP TABLE IF EXISTS komentars")
        db.execSQL("DROP TABLE IF EXISTS pendapatans")
        db.execSQL("DROP TABLE IF EXISTS beritas")
        db.execSQL("DROP TABLE IF EXISTS kategoris")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }
}