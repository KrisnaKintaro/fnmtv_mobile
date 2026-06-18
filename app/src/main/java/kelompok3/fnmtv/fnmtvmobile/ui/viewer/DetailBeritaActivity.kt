package kelompok3.fnmtv.fnmtvmobile.ui.viewer

import android.content.Intent
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
    private var beritaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBeritaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }

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
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Menutup activity detail dan kembali ke halaman sebelumnya
                true
            }
            else -> super.onOptionsItemSelected(item)
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
        // Reaksi menggunakan bahasa Indonesia sesuai dengan ReaksiController.php
        binding.btnLike.setOnClickListener { handleReaction(slug, "suka") }
        binding.btnLove.setOnClickListener { handleReaction(slug, "cinta") }
        binding.btnWow.setOnClickListener  { handleReaction(slug, "kaget") }

        binding.btnKirimKomentar.setOnClickListener {
            val token = sessionManager.fetchAuthToken()
            if (token.isNullOrEmpty()) {
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

                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        beritaId = it.id
                        displayBerita(it)
                    }
                } else {
                    Log.e("API_ERROR", "Gagal load berita: ${response.errorBody()?.string()}")
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

        // ✅ PERBAIKAN FINAL: Menggunakan syntax [key] untuk menghindari konflik library
        val rekap = berita.reaksiRekap
        binding.btnLike.text = "👍 ${rekap?.suka ?: 0}"
        binding.btnLove.text = "❤️ ${rekap?.cinta ?: 0}"
        binding.btnWow.text  = "😲 ${rekap?.kaget ?: 0}"

        val listKomentar = berita.komentar ?: emptyList()
        komentarAdapter.updateData(listKomentar)

        binding.tvEmptyKomentar.visibility = if (listKomentar.isEmpty()) View.VISIBLE else View.GONE
        binding.rvKomentar.visibility = if (listKomentar.isEmpty()) View.GONE else View.VISIBLE

        val imgUrl = if (berita.fotoThumbnail?.startsWith("http") == true) berita.fotoThumbnail
        else "https://baru.fenomenatv.com/uploads/thumbnail/${berita.fotoThumbnail}"

        Glide.with(this).load(imgUrl).centerCrop().into(binding.ivDetailThumbnail)
    }

    private fun postReaksi(slug: String, jenis: String) {
        val id = beritaId ?: return
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@DetailBeritaActivity)
                    .kirimReaksi(token, id, jenis)

                if (response.isSuccessful) {
                    Toast.makeText(this@DetailBeritaActivity, "Reaksi berhasil!", Toast.LENGTH_SHORT).show()
                    fetchDetailBerita(slug)
                } else {
                    Log.e("REAKSI_DEBUG", "Gagal: ${response.errorBody()?.string()}")
                    Toast.makeText(this@DetailBeritaActivity, "Gagal mengirim reaksi", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("REAKSI_ERROR", e.message.toString())
            }
        }
    }

    private fun postKomentar(slug: String, isi: String) {
        val id = beritaId ?: return
        val token = "Bearer ${sessionManager.fetchAuthToken()}"

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@DetailBeritaActivity)
                    .kirimKomentar(token, id, isi)

                if (response.isSuccessful) {
                    Toast.makeText(this@DetailBeritaActivity, "Komentar terkirim!", Toast.LENGTH_LONG).show()
                    binding.etKomentar.text.clear()
                    fetchDetailBerita(slug)
                } else {
                    Log.e("KOMENTAR_DEBUG", "Gagal: ${response.errorBody()?.string()}")
                    Toast.makeText(this@DetailBeritaActivity, "Gagal mengirim komentar", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("KOMENTAR_ERROR", e.message.toString())
            }
        }
    }
}