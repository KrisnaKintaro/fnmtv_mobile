package kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin

import android.content.Context
import android.database.Cursor
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita

class AnalitikController(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // --- FUNGSI BANTUAN ANTI-CRASH (Biar aman ngambil data dari Cursor) ---
    private fun getIntSafe(cursor: Cursor, columnName: String): Int {
        val index = cursor.getColumnIndex(columnName)
        return if (index != -1 && !cursor.isNull(index)) cursor.getInt(index) else 0
    }

    private fun getStringSafe(cursor: Cursor, columnName: String): String {
        val index = cursor.getColumnIndex(columnName)
        return if (index != -1 && !cursor.isNull(index)) cursor.getString(index) else ""
    }

    // Helper Filter Periode Waktu
    private fun getWhereTimeClause(periode: String, tablePrefix: String = ""): String {
        val col = if (tablePrefix.isNotEmpty()) "$tablePrefix.created_at" else "created_at"
        return when (periode) {
            "Hari Ini" -> "DATE($col) = DATE('now', 'localtime')"
            "7 Hari" -> "DATE($col) >= DATE('now', '-6 days', 'localtime')"
            "30 Hari" -> "DATE($col) >= DATE('now', '-29 days', 'localtime')"
            "1 Tahun" -> "DATE($col) >= DATE('now', '-1 year', 'localtime')"
            else -> "1=1" // Semua
        }
    }

    // 1. SUMMARY STATS
    fun getSummaryData(periode: String): Map<String, Any> {
        var views = 0
        var berita = 0
        var komentar = 0
        var pendapatan = 0.0

        try {
            val db = dbHelper.readableDatabase
            val timeClause = getWhereTimeClause(periode)

            try { db.rawQuery("SELECT COUNT(*) FROM view_logs WHERE $timeClause", null).use { if (it.moveToFirst()) views = it.getInt(0) } } catch (e: Exception) { e.printStackTrace() }

            // FIX: Hapus "OR status = 'Published'" karena SQLite bakal error kalau kolom 'status' gak ada di tabel beritas
            try { db.rawQuery("SELECT COUNT(*) FROM beritas WHERE status_berita = 'Published' AND $timeClause", null).use { if (it.moveToFirst()) berita = it.getInt(0) } } catch (e: Exception) { e.printStackTrace() }

            try { db.rawQuery("SELECT COUNT(*) FROM komentars WHERE $timeClause", null).use { if (it.moveToFirst()) komentar = it.getInt(0) } } catch (e: Exception) { e.printStackTrace() }

            try {
                // FIX: Pakai kolom resmi (berita_id) tanpa OR
                val pClause = getWhereTimeClause(periode, "p")
                val qPendapatan = """
                    SELECT SUM(p.nominal_pendapatan) FROM pendapatans p 
                    JOIN beritas b ON p.berita_id = b.id 
                    WHERE b.status_berita = 'Published' 
                    AND p.status_pembayaran = 'Paid' AND $pClause
                """.trimIndent()
                db.rawQuery(qPendapatan, null).use { if (it.moveToFirst()) pendapatan = it.getDouble(0) }
            } catch (e: Exception) { e.printStackTrace() }

        } catch (e: Exception) { e.printStackTrace() }

        return mapOf("views" to views, "berita" to berita, "komentar" to komentar, "pendapatan" to pendapatan)
    }

    // 2. TREND CHART DATA
    fun getTrendChartData(metric: String, periode: String): List<Pair<String, Float>> {
        val result = mutableListOf<Pair<String, Float>>()
        try {
            val db = dbHelper.readableDatabase
            val table = when(metric) {
                "Views" -> "view_logs"
                "Pengunjung" -> "view_logs"
                else -> "komentars"
            }

            // FIX: ipAddress diganti jadi ip_address sesuai DatabaseHelper lu
            val columnCount = if (metric == "Pengunjung") "COUNT(DISTINCT ip_address)" else "COUNT(*)"

            val (sqlFormat, limit) = when(periode) {
                "Hari Ini" -> Pair("strftime('%H:00', created_at)", 24)
                "7 Hari" -> Pair("strftime('%d/%m', created_at)", 7)
                "30 Hari" -> Pair("strftime('%W', created_at)", 4)
                "1 Tahun" -> Pair("strftime('%m', created_at)", 12)
                else -> Pair("strftime('%Y', created_at)", 10)
            }

            val query = """
                SELECT $sqlFormat as label, $columnCount as total 
                FROM $table 
                WHERE ${getWhereTimeClause(periode)}
                GROUP BY label ORDER BY created_at ASC LIMIT $limit
            """.trimIndent()

            db.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val label = getStringSafe(cursor, "label")
                        val total = getIntSafe(cursor, "total").toFloat()
                        if (label.isNotEmpty()) result.add(label to total)
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    // 3. TOP 10 NEWS
    // --- UPDATE FUNGSI TOP 10 NEWS DI AnalitikController.kt ---
    fun getTop10News(): List<Berita> {
        val list = mutableListOf<Berita>()
        try {
            val db = dbHelper.readableDatabase

            // Query untuk menghitung masing-masing metrik secara real dari tabel logs
            val query = """
                SELECT b.*, 
                (SELECT COUNT(*) FROM view_logs v WHERE v.berita_id = b.id) as v_count,
                (SELECT COUNT(*) FROM komentars k WHERE k.berita_id = b.id) as k_count,
                (SELECT COUNT(*) FROM reaksis r WHERE r.berita_id = b.id) as r_count
                FROM beritas b 
                WHERE b.status_berita = 'Published'
                ORDER BY (v_count + k_count + r_count) DESC LIMIT 10
            """.trimIndent()

            db.rawQuery(query, null).use { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val views = getIntSafe(cursor, "v_count")
                        val komen = getIntSafe(cursor, "k_count")
                        val reaksi = getIntSafe(cursor, "r_count")

                        // Susun string sesuai permintaan: jumlah view | jumlah komentar | jumlah reaksi
                        val detailReal = "$views View | $komen Komentar | $reaksi Reaksi"

                        list.add(Berita(
                            id = getIntSafe(cursor, "id"),
                            user_id = getIntSafe(cursor, "user_id"),
                            kategori_id = getIntSafe(cursor, "kategori_id"),
                            judul_berita = getStringSafe(cursor, "judul_berita"),
                            slug = getStringSafe(cursor, "slug"),
                            isi_berita = getStringSafe(cursor, "isi_berita"),
                            foto_thumbnail = getStringSafe(cursor, "foto_thumbnail"),
                            status_berita = getStringSafe(cursor, "status_berita"),
                            nama_penulis = detailReal // Kita simpan rincian data di sini cuy
                        ))
                    } while (cursor.moveToNext())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    // 4. HEATMAP DATA (REVISI PAS 7 HARI)
    fun getHeatmapData(periode: String): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()
        try {
            val db = dbHelper.readableDatabase
            val sdfDb = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val cal = java.util.Calendar.getInstance()

            when (periode) {
                "Hari Ini", "7 Hari" -> {
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -6)
                    val skeleton = mutableMapOf<String, Int>()
                    val labels = mutableMapOf<String, String>()
                    val sdfLabel = java.text.SimpleDateFormat("dd/MM\nEEE", java.util.Locale("id", "ID")) // Format: 15/04 \n Sen

                    for (i in 0..6) {
                        val dStr = sdfDb.format(cal.time)
                        skeleton[dStr] = 0
                        labels[dStr] = sdfLabel.format(cal.time)
                        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }

                    val query = """
                        SELECT DATE(created_at) as tgl, COUNT(*) as qty FROM beritas 
                        WHERE status_berita = 'Published' AND DATE(created_at) >= DATE('now', '-6 days', 'localtime') GROUP BY tgl
                    """.trimIndent()

                    db.rawQuery(query, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                            do {
                                val tgl = getStringSafe(cursor, "tgl")
                                if (skeleton.containsKey(tgl)) skeleton[tgl] = getIntSafe(cursor, "qty")
                            } while (cursor.moveToNext())
                        }
                    }
                    skeleton.forEach { (k, v) -> result.add(Pair(labels[k] ?: "", v)) }
                }

                "30 Hari" -> {
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -29)
                    val skeleton = mutableMapOf<String, Int>()
                    val labels = mutableMapOf<String, String>()
                    val sdfLabel = java.text.SimpleDateFormat("dd", java.util.Locale("id", "ID")) // Format Kalender: 15

                    for (i in 0..29) {
                        val dStr = sdfDb.format(cal.time)
                        skeleton[dStr] = 0
                        labels[dStr] = sdfLabel.format(cal.time)
                        cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }

                    val query = """
                        SELECT DATE(created_at) as tgl, COUNT(*) as qty FROM beritas 
                        WHERE status_berita = 'Published' AND DATE(created_at) >= DATE('now', '-29 days', 'localtime') GROUP BY tgl
                    """.trimIndent()

                    db.rawQuery(query, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                            do {
                                val tgl = getStringSafe(cursor, "tgl")
                                if (skeleton.containsKey(tgl)) skeleton[tgl] = getIntSafe(cursor, "qty")
                            } while (cursor.moveToNext())
                        }
                    }
                    skeleton.forEach { (k, v) -> result.add(Pair(labels[k] ?: "", v)) }
                }

                "1 Tahun" -> {
                    val skeleton = mutableMapOf<String, Int>()
                    val bulanNames = arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des")

                    for (i in 1..12) skeleton[i.toString().padStart(2, '0')] = 0

                    val query = """
                        SELECT strftime('%m', created_at) as bulan, COUNT(*) as qty FROM beritas 
                        WHERE status_berita = 'Published' AND strftime('%Y', created_at) = strftime('%Y', 'now', 'localtime') GROUP BY bulan
                    """.trimIndent()

                    db.rawQuery(query, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                            do {
                                val bulan = getStringSafe(cursor, "bulan")
                                if (skeleton.containsKey(bulan)) skeleton[bulan] = getIntSafe(cursor, "qty")
                            } while (cursor.moveToNext())
                        }
                    }
                    skeleton.forEach { (k, v) -> result.add(Pair(bulanNames[k.toInt() - 1], v)) }
                }

                "Semua" -> {
                    val query = """
                        SELECT strftime('%Y', created_at) as tahun, COUNT(*) as qty FROM beritas 
                        WHERE status_berita = 'Published' GROUP BY tahun ORDER BY tahun ASC
                    """.trimIndent()

                    db.rawQuery(query, null).use { cursor ->
                        if (cursor.moveToFirst()) {
                            do {
                                val tahun = getStringSafe(cursor, "tahun")
                                val qty = getIntSafe(cursor, "qty")
                                // Langsung masukin ke result karena kita cuma butuh tahun yang ada datanya aja
                                if (tahun.isNotEmpty()) {
                                    result.add(Pair(tahun, qty))
                                }
                            } while (cursor.moveToNext())
                        }
                    }
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
        return result
    }
}