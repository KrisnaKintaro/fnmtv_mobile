package kelompok3.fnmtv.fnmtvmobile.ui.viewer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.local.SessionManager
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityDetailBeritaBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.KomentarAdapter
import kelompok3.fnmtv.fnmtvmobile.ui.auth.LoginActivity
import kotlinx.coroutines.launch

class DetailBeritaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBeritaBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var komentarAdapter: KomentarAdapter
    private var beritaSlug: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }

        // 2. Ambil Slug dari Intent
        beritaSlug = intent.getStringExtra("BERITA_SLUG")

        if (beritaSlug != null) {
            setupRecyclerViewKomentar()
            setupActionListeners(beritaSlug!!)
            fetchDetailBerita(beritaSlug!!)
        } else {
            Toast.makeText(this, "Berita tidak ditemukan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupRecyclerViewKomentar() {
        komentarAdapter = KomentarAdapter()
        binding.rvKomentar.apply {
            layoutManager = LinearLayoutManager(this@DetailBeritaActivity)
            adapter = komentarAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupActionListeners(slug: String) {
        binding.btnLike.setOnClickListener { handleReaction(slug, "like") }
        binding.btnLove.setOnClickListener { handleReaction(slug, "love") }
        binding.btnWow.setOnClickListener { handleReaction(slug, "wow") }

        binding.btnKirimKomentar.setOnClickListener {
            if (sessionManager.fetchAuthToken().isNullOrEmpty()) {
                showLoginRequiredDialog()
            } else {
                val textKomentar = binding.etKomentar.text.toString().trim()
                if (textKomentar.isNotEmpty()) {
                    postKomentar(slug, textKomentar)
                } else {
                    Toast.makeText(this, "Tulis komentar terlebih dahulu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleReaction(slug: String, jenis: String) {
        if (sessionManager.fetchAuthToken().isNullOrEmpty()) {
            showLoginRequiredDialog()
        } else {
            postReaksi(slug, jenis)
        }
    }

    private fun showLoginRequiredDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_login_required, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btnNantiAja).setOnClickListener { dialog.dismiss() }
        dialogView.findViewById<Button>(R.id.btnGasLogin).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        dialog.show()
    }

    private fun fetchDetailBerita(slug: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@DetailBeritaActivity).getDetailBerita(slug)
                if (response.isSuccessful && response.body()?.status == "success") {
                    response.body()?.data?.let { displayBerita(it) }
                }
            } catch (e: Exception) {
                Log.e("DETAIL_ERROR", e.message.toString())
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun displayBerita(berita: BeritaItem) {
        binding.tvDetailKategori.text = berita.kategori?.namaKategori?.uppercase() ?: "UMUM"
        binding.tvDetailJudul.text = berita.judulBerita
        binding.tvDetailAuthor.text = "Oleh: ${berita.user?.username ?: "Admin FNM"}"
        binding.tvDetailTanggal.text = berita.waktuPublikasi?.substringBefore("T") ?: ""
        binding.tvDetailViews.text = "👁 ${berita.jumlahView ?: "0"} views"
        
        val htmlContent = berita.isiBerita ?: "Tidak ada konten."
        binding.tvDetailContent.text = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.btnLike.text = "👍 ${reflectCount(berita, "getLikeCount")}"
        binding.btnLove.text = "❤️ ${reflectCount(berita, "getLoveCount")}"
        binding.btnWow.text = "😲 ${reflectCount(berita, "getWowCount")}"

        try {
            val list = berita.javaClass.getMethod("getKomentar").invoke(berita) as? List<Any> ?: listOf()
            if (list.isEmpty()) {
                binding.tvEmptyKomentar.visibility = View.VISIBLE
                binding.rvKomentar.visibility = View.GONE
            } else {
                binding.tvEmptyKomentar.visibility = View.GONE
                binding.rvKomentar.visibility = View.VISIBLE
                komentarAdapter.updateData(list)
            }
        } catch (e: Exception) {
            binding.tvEmptyKomentar.visibility = View.VISIBLE
        }

        val imgUrl = if (berita.fotoThumbnail?.startsWith("http") == true) berita.fotoThumbnail 
                     else "https://baru.fenomenatv.com/uploads/thumbnail/${berita.fotoThumbnail}"

        Glide.with(this).load(imgUrl).centerCrop().placeholder(android.R.drawable.ic_menu_gallery).into(binding.ivDetailThumbnail)
    }

    private fun postReaksi(slug: String, jenis: String) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        val requestBody = mapOf(
            "slug" to slug,
            "jenis_reaksi" to jenis
        )
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@DetailBeritaActivity)
                    .kirimReaksi(token, requestBody)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailBeritaActivity, "Reaksi terkirim!", Toast.LENGTH_SHORT).show()
                    fetchDetailBerita(slug)
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailBeritaActivity, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun postKomentar(slug: String, isi: String) {
        val token = "Bearer ${sessionManager.fetchAuthToken()}"
        val requestBody = mapOf(
            "slug" to slug,
            "isi_komentar" to isi
        )
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@DetailBeritaActivity)
                    .kirimKomentar(token, requestBody)
                if (response.isSuccessful) {
                    Toast.makeText(this@DetailBeritaActivity, "Komentar terkirim!", Toast.LENGTH_SHORT).show()
                    binding.etKomentar.text.clear()
                    fetchDetailBerita(slug)
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailBeritaActivity, "Gagal mengirim komentar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reflectCount(obj: Any, methodName: String): Int {
        return try { obj.javaClass.getMethod(methodName).invoke(obj) as? Int ?: 0 } catch (e: Exception) { 0 }
    }
}
