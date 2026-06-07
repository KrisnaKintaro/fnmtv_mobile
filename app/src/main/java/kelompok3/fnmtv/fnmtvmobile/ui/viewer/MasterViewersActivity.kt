package kelompok3.fnmtv.fnmtvmobile.ui.viewer

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
import kelompok3.fnmtv.fnmtvmobile.ui.auth.RegisterActivity
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.HomeFragment
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.KategoriFragment
import kotlinx.coroutines.launch

class MasterViewersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterViewersBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterViewersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Pasang Home Fragment pas pertama buka
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        sessionManager = SessionManager(this)
        kelolaKondisiNavbarUser()
        fetchKategoriDariApi()

        // 🚧 BLUEPRINT: [beritahasilsearch.blade.php]
        // TODO: Tangkap event klik tombol pencarian di layout XML
        binding.btnSearchSubmit.setOnClickListener {
            val keyword = binding.etSearch.text.toString()
            if (keyword.isNotEmpty()) {
                // Lempar ke SearchActivity / SearchFragment bawa keyword-nya
                Toast.makeText(this, "Mencari: $keyword (Halaman belum dibuat)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun kelolaKondisiNavbarUser() {
        val cekToken = sessionManager.fetchAuthToken()

        if (!cekToken.isNullOrEmpty()) {
            binding.layoutAuthButtons.visibility = android.view.View.GONE
            binding.btnProfile.visibility = android.view.View.VISIBLE

            binding.btnProfile.setOnClickListener { objekLayar ->
                val popupMenu = androidx.appcompat.widget.PopupMenu(this, objekLayar)
                popupMenu.menu.add("Edit Profil")
                popupMenu.menu.add("Keluar / Logout")

                popupMenu.setOnMenuItemClickListener { itemPilihan ->
                    when (itemPilihan.title) {
                        "Edit Profil" -> {
                            // 🚧 BLUEPRINT: [userprofil.blade.php]
                            // TODO: Buka UserProfileActivity buat form update profil & ganti password
                            Toast.makeText(this, "Membuka Menu Profil... (Halaman belum dibuat)", Toast.LENGTH_SHORT).show()
                            true
                        }
                        "Keluar / Logout" -> {
                            Toast.makeText(this@MasterViewersActivity, "Sedang keluar...", Toast.LENGTH_SHORT).show()

                            lifecycleScope.launch {
                                try {
                                    ApiClient.getApiService(this@MasterViewersActivity).logoutUser()
                                } catch (e: Exception) {
                                    Log.e("LOGOUT_API", "Server gagal dihubungi, tapi sesi lokal tetap dihapus")
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
            binding.layoutAuthButtons.visibility = android.view.View.VISIBLE
            binding.btnProfile.visibility = android.view.View.GONE

            binding.btnHeaderLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            binding.btnHeaderRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    private fun fetchKategoriDariApi() {
        lifecycleScope.launch {
            var isSuccess = false

            // Terus muter sampai isSuccess jadi true
            while (!isSuccess) {
                try {
                    val response = ApiClient.getApiService(this@MasterViewersActivity).getKategori()

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val daftarKategori = response.body()?.data ?: emptyList()
                        setupNavbar(daftarKategori)
                        setupFooter(daftarKategori)

                        isSuccess = true
                    } else {
                        Log.e("API_RETRY", "Gagal dapet kategori, mencoba lagi dalam 2 detik...")
                        kotlinx.coroutines.delay(2000)
                    }
                } catch (e: Exception) {
                    Log.e("API_ERROR", "Koneksi Error: ${e.message}. mencoba lagi dalam 2 detik...")
                    kotlinx.coroutines.delay(2000)
                }
            }
        }
    }

    private fun setupNavbar(kategoriList: List<KategoriItem>) {
        val tabLayout = binding.tabLayoutKategori
        tabLayout.removeAllTabs()

        tabLayout.addTab(tabLayout.newTab().setText("HOME"))

        for (kategori in kategoriList) {
            // Kita simpan ID atau Slug di tag biar nanti gampang ditarik pas diklik
            val tab = tabLayout.newTab().setText(kategori.namaKategori).setTag(kategori.slug)
            tabLayout.addTab(tab)
        }

        // 🚧 BLUEPRINT: [tampilantiapkategori.blade.php]
        // TODO: Bikin aksi pas tab kategori diklik
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()

                if (tab?.text == "HOME") {
                    // Kembali ke Home yang ada Headline & Trending
                    transaction.replace(R.id.fragment_container, HomeFragment())
                } else {
                    // Pindah ke halaman Kategori Berita
                    val slugKategori = tab?.tag as? String ?: ""
                    val namaKategori = tab?.text.toString()
                    transaction.replace(R.id.fragment_container, KategoriFragment.newInstance(slugKategori, namaKategori))
                }
                transaction.commit()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupFooter(kategoriList: List<KategoriItem>) {
        val footerCategoryContainer = findViewById<LinearLayout>(R.id.footerCategoryContainer)

        if (footerCategoryContainer != null) {
            footerCategoryContainer.removeAllViews()
            val batasKategori = kategoriList.take(4)

            for (kategori in batasKategori) {
                val textView = TextView(this).apply {
                    text = kategori.namaKategori
                    setTextColor(android.graphics.Color.parseColor("#CCCCCC"))
                    textSize = 13f
                    textAlignment = android.view.View.TEXT_ALIGNMENT_CENTER
                    setPadding(0, 0, 0, 24)
                }
                footerCategoryContainer.addView(textView)
            }
        }
    }
}
