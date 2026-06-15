package kelompok3.fnmtv.fnmtvmobile.ui.viewer

/**
 * FILE DIPERBARUI
 * Lokasi: app/src/main/java/kelompok3/fnmtv/fnmtvmobile/ui/viewer/MasterViewersActivity.kt
 *
 * Perubahan dari versi sebelumnya:
 *  1. setupNavbar() — tag tab sekarang menyimpan Pair(slug, nama), bukan hanya slug
 *  2. onTabSelected() — sekarang benar-benar replace fragment ke KategoriFragment
 *  3. Import KategoriFragment ditambahkan
 */

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.local.SessionManager
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityMasterViewersBinding
import kelompok3.fnmtv.fnmtvmobile.ui.auth.LoginActivity
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.HomeFragment
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.KategoriFragment  // ← IMPORT BARU
import kotlinx.coroutines.launch

class MasterViewersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterViewersBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterViewersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pasang HomeFragment saat pertama kali dibuka
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        sessionManager = SessionManager(this)
        kelolaKondisiNavbarUser()
        fetchKategoriDariApi()

        // ── Pencarian ──────────────────────────────────────────────────────────
        // 🚧 BLUEPRINT: [beritahasilsearch.blade.php]
        // TODO: Buat SearchFragment / SearchActivity, lalu lempar keyword ke sana
        binding.btnSearchSubmit.setOnClickListener {
            val keyword = binding.etSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                Toast.makeText(this, "Mencari: $keyword (Halaman belum dibuat)", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Ketik kata kunci dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Navbar auth (login/profil) ─────────────────────────────────────────────
    private fun kelolaKondisiNavbarUser() {
        val cekToken = sessionManager.fetchAuthToken()

        if (!cekToken.isNullOrEmpty()) {
            // User sudah login → tampilkan ikon profil
            binding.layoutAuthButtons.visibility = android.view.View.GONE
            binding.btnProfile.visibility        = android.view.View.VISIBLE

            binding.btnProfile.setOnClickListener { anchor ->
                val popupMenu = androidx.appcompat.widget.PopupMenu(this, anchor)
                popupMenu.menu.add("Edit Profil")
                popupMenu.menu.add("Keluar / Logout")

                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Edit Profil" -> {
                            // 🚧 BLUEPRINT: [userprofil.blade.php]
                            // TODO: Buka UserProfileActivity
                            Toast.makeText(this, "Membuka Profil... (Belum dibuat)", Toast.LENGTH_SHORT).show()
                            true
                        }
                        "Keluar / Logout" -> {
                            Toast.makeText(this, "Sedang keluar...", Toast.LENGTH_SHORT).show()
                            lifecycleScope.launch {
                                try {
                                    ApiClient.getApiService(this@MasterViewersActivity).logoutUser()
                                } catch (e: Exception) {
                                    Log.e("LOGOUT_API", "Server gagal, sesi lokal tetap dihapus: ${e.message}")
                                } finally {
                                    sessionManager.clearSession()
                                    Toast.makeText(this@MasterViewersActivity, "Berhasil Keluar!", Toast.LENGTH_SHORT).show()
                                    kelolaKondisiNavbarUser()
                                }
                            }
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }

        } else {
            // User belum login → tampilkan tombol Masuk & Daftar
            binding.layoutAuthButtons.visibility = android.view.View.VISIBLE
            binding.btnProfile.visibility        = android.view.View.GONE

            binding.btnHeaderLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            binding.btnHeaderRegister.setOnClickListener {
                // 🚧 BLUEPRINT: RegisterActivity
                // TODO: Buka RegisterActivity
                Toast.makeText(this, "Halaman Register belum dibuat!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Fetch kategori dari API ────────────────────────────────────────────────
    private fun fetchKategoriDariApi() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@MasterViewersActivity).getKategori()
                if (response.isSuccessful && response.body()?.status == "success") {
                    val daftarKategori = response.body()?.data ?: emptyList()
                    setupNavbar(daftarKategori)
                    setupFooter(daftarKategori)
                } else {
                    Toast.makeText(this@MasterViewersActivity, "Gagal ambil kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Koneksi Error: ${e.message}")
                Toast.makeText(this@MasterViewersActivity, "Cek koneksi internet!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Setup tab navbar kategori ─────────────────────────────────────────────
    /**
     * PERUBAHAN UTAMA ADA DI SINI:
     *
     * Sebelumnya: tag hanya menyimpan slug (String)
     * Sekarang  : tag menyimpan Pair(slug, namaKategori) supaya keduanya
     *             bisa diakses saat tab diklik.
     *
     * Saat tab diklik:
     *  - "HOME"     → ganti fragment ke HomeFragment
     *  - Lainnya    → ganti fragment ke KategoriFragment.newInstance(slug, nama)
     */
    private fun setupNavbar(kategoriList: List<KategoriItem>) {
        val tabLayout = binding.tabLayoutKategori
        tabLayout.removeAllTabs()

        // Tab pertama selalu HOME
        tabLayout.addTab(tabLayout.newTab().setText("HOME"))

        // Satu tab per kategori
        for (kategori in kategoriList) {
            val tab = tabLayout.newTab()
                .setText(kategori.namaKategori)
                .setTag(Pair(kategori.slug, kategori.namaKategori))  // ← simpan keduanya
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when {
                    // Klik tab HOME
                    tab?.text == "HOME" -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment())
                            .commit()
                    }

                    // Klik tab kategori
                    else -> {
                        @Suppress("UNCHECKED_CAST")
                        val tagData = tab?.tag as? Pair<String, String>
                        val slug    = tagData?.first  ?: return
                        val nama    = tagData?.second ?: slug

                        supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container,
                                KategoriFragment.newInstance(slug, nama)  // ← FRAGMENT BARU
                            )
                            .commit()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // ── Setup footer ──────────────────────────────────────────────────────────
    private fun setupFooter(kategoriList: List<KategoriItem>) {
        val footerCategoryContainer = findViewById<LinearLayout>(R.id.footerCategoryContainer)

        if (footerCategoryContainer != null) {
            footerCategoryContainer.removeAllViews()
            val batasKategori = kategoriList.take(4)

            for (kategori in batasKategori) {
                val textView = TextView(this).apply {
                    text          = kategori.namaKategori
                    setTextColor(android.graphics.Color.parseColor("#CCCCCC"))
                    textSize      = 13f
                    textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 0, 0, 24)
                }
                footerCategoryContainer.addView(textView)
            }
        }
    }
}