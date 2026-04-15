package kelompok3.fnmtv.fnmtvmobile.Database.Migration

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class DatabaseSeeder(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Helper buat bikin tanggal acak antara hari ini dan X hari ke belakang
    private fun getRandomDate(daysBack: Int = 14): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(0..daysBack).random())
        cal.add(Calendar.HOUR_OF_DAY, -(0..23).random())
        cal.add(Calendar.MINUTE, -(0..59).random())
        return sdf.format(cal.time)
    }

    fun run() {
        val db = dbHelper.writableDatabase

        try {
            // Cek apakah data berita sudah ada
            val cursor = db.rawQuery("SELECT COUNT(*) FROM beritas", null)
            var count = 0
            if (cursor.moveToFirst()) count = cursor.getInt(0)
            cursor.close()

            if (count > 0) {
                Toast.makeText(context, "Data sudah ada ($count Berita). Seeder dibatalkan.", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Tabel belum siap!", Toast.LENGTH_SHORT).show()
            return
        }

        db.beginTransaction()
        try {
            Toast.makeText(context, "⏳ Menyuntik data 2 minggu terakhir...", Toast.LENGTH_LONG).show()

            // 1. SEED KATEGORI (7 Kategori)
            val kategoriList = listOf("Teknologi", "Olahraga", "Politik", "Hiburan", "Ekonomi", "Otomotif", "Kesehatan")
            kategoriList.forEach { nama ->
                val valKat = ContentValues().apply {
                    put("nama_kategori", nama)
                    put("slug", nama.lowercase().replace(" ", "-"))
                    put("created_at", getRandomDate(14))
                }
                db.insert("kategoris", null, valKat)
            }

            // 2. SEED USERS (10 User berbagai Role)
            val roles = listOf("Admin", "Editor", "Redaksi", "Viewer", "Viewer", "Viewer", "Viewer", "Editor", "Redaksi", "Viewer")
            roles.forEachIndexed { index, role ->
                val valUser = ContentValues().apply {
                    put("username", "user_$index")
                    put("email", "user$index@fnmtv.com")
                    put("password", "12345")
                    put("role", role)
                    put("status", "Aktif")
                    put("created_at", getRandomDate(14))
                }
                db.insert("users", null, valUser)
            }

            // 3. SEED BERITAS (40 Berita yang disebar 14 hari terakhir)
            for (i in 1..40) {
                val publishDate = getRandomDate(14)
                val statusList = listOf("Published", "Published", "Published", "Draft", "Pending", "Rejected")
                val status = statusList.random()

                val valBerita = ContentValues().apply {
                    put("user_id", (1..10).random())
                    put("kategori_id", (1..kategoriList.size).random())
                    put("judul_berita", "Berita Viral FNM TV Ke-$i Menjadi Sorotan")
                    put("slug", "berita-viral-fnmtv-ke-$i")
                    put("isi_berita", "Ini adalah simulasi isi berita yang sangat panjang untuk keperluan analitik 14 hari.")
                    put("foto_thumbnail", "thumbnail_$i.jpg")
                    put("status_berita", status)
                    put("created_at", publishDate)
                    if (status == "Published") put("waktu_publikasi", publishDate)
                }
                db.insert("beritas", null, valBerita)
            }

            // 4. SEED VIEW LOGS (800 Tayangan acak)
            for (i in 1..800) {
                val valView = ContentValues().apply {
                    put("berita_id", (1..40).random())
                    put("ip_address", "192.168.${(1..10).random()}.${(1..200).random()}")
                    put("created_at", getRandomDate(14))
                }
                db.insert("view_logs", null, valView)
            }

            // 5. SEED KOMENTARS (150 Komentar)
            val statusKomen = listOf("Approved", "Approved", "Pending", "Spam")
            for (i in 1..150) {
                val valKomen = ContentValues().apply {
                    put("berita_id", (1..40).random())
                    put("user_id", (1..10).random())
                    put("isi_komentar", "Wah berita ke-$i ini sangat informatif, terima kasih FNM TV!")
                    put("status_moderasi", statusKomen.random())
                    put("created_at", getRandomDate(14))
                }
                db.insert("komentars", null, valKomen)
            }

            // 6. SEED REAKSIS (300 Reaksi)
            val jenisReaksi = listOf("Like", "Love", "Haha", "Wow", "Sad", "Angry")
            for (i in 1..300) {
                val valReaksi = ContentValues().apply {
                    put("berita_id", (1..40).random())
                    put("user_id", (1..10).random())
                    put("jenis_reaksi", jenisReaksi.random())
                    put("created_at", getRandomDate(14))
                }
                db.insert("reaksis", null, valReaksi)
            }

            // 7. SEED PENDAPATANS (40 Data Pembayaran)
            for (i in 1..40) {
                val paymentDate = getRandomDate(14)
                val statusPay = listOf("Paid", "Paid", "Unpaid").random()
                val valPendapatan = ContentValues().apply {
                    put("berita_id", i)
                    put("user_id", (1..3).random()) // Anggap user 1-3 itu Editor/Redaksi
                    put("nominal_pendapatan", (50..500).random() * 1000.0) // 50.000 sampai 500.000
                    put("status_pembayaran", statusPay)
                    put("created_at", paymentDate)
                    if (statusPay == "Paid") put("waktu_pembayaran", paymentDate)
                }
                db.insert("pendapatans", null, valPendapatan)
            }

            db.setTransactionSuccessful()
            Toast.makeText(context, "✅ 14 HARI SIMULASI DATA BERHASIL DISUNTIK!", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("SEEDER_ERROR", "Error detail: ", e)
            Toast.makeText(context, "❌ Gagal Seeding: Cek Logcat!", Toast.LENGTH_LONG).show()
        } finally {
            db.endTransaction()
            db.close()
        }
    }
}