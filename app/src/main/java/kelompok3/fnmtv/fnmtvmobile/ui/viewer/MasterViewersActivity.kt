package kelompok3.fnmtv.fnmtvmobile.ui.viewer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.local.SessionManager
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityMasterViewersBinding
import kelompok3.fnmtv.fnmtvmobile.ui.auth.LoginActivity
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.HomeFragment
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
        // Mulai tarik data kategori dari API
        fetchKategoriDariApi()
    }

    private fun kelolaKondisiNavbarUser() {
        val cekToken = sessionManager.fetchAuthToken()

        // JIKA USER SUDAH LOGIN (Token tersimpan di memori HP)
        if (!cekToken.isNullOrEmpty()) {

            // Sembunyikan bungkusan tombol "Masuk" & "Daftar" (Grup Linear Lu)
            binding.layoutAuthButtons.visibility = android.view.View.GONE

            // Nyalakan Ikon Profil bunder yang ada di sebelah kanan
            binding.btnProfile.visibility = android.view.View.VISIBLE

            // Aksi ketika Ikon Profil diklik (Memunculkan Option Menu ala Popup)
            binding.btnProfile.setOnClickListener { objekLayar ->
                val popupMenu = androidx.appcompat.widget.PopupMenu(this, objekLayar)
                popupMenu.menu.add("Edit Profil")
                popupMenu.menu.add("Keluar / Logout")

                popupMenu.setOnMenuItemClickListener { itemPilihan ->
                    when (itemPilihan.title) {
                        "Edit Profil" -> {
                            Toast.makeText(this, "Membuka Menu Profil...", Toast.LENGTH_SHORT).show()
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
                popupMenu.show() // Tampilkan menunya ke layar
            }

        } else {
            // JIKA GUEST USER (Belum Login / Habis Logout)
            binding.layoutAuthButtons.visibility = android.view.View.VISIBLE
            binding.btnProfile.visibility = android.view.View.GONE

            // Tombol "Masuk" di navbar diklik -> Lempar ke halaman LoginActivity
            binding.btnHeaderLogin.setOnClickListener {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            binding.btnHeaderRegister.setOnClickListener {
                Toast.makeText(this, "Halaman Register belum kita bikin gank!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchKategoriDariApi() {
        // Nembak API nggak boleh di Main Thread biar HP ga nge-freeze, jadi kita pakai Coroutines
        lifecycleScope.launch {
            try {
                // Panggil ApiClient yang udah lu bikin di awal
                val response = ApiClient.getApiService(this@MasterViewersActivity).getKategori()

                if (response.isSuccessful && response.body()?.status == "success") {
                    val daftarKategori = response.body()?.data ?: emptyList()

                    // Kalau sukses dapet data, pasang ke Navbar dan Footer!
                    setupNavbar(daftarKategori)
                    setupFooter(daftarKategori)

                } else {
                    Toast.makeText(this@MasterViewersActivity, "Gagal ambil kategori", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Koneksi Error: ${e.message}")
                Toast.makeText(this@MasterViewersActivity, "Cek koneksi internet cuy!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNavbar(kategoriList: List<KategoriItem>) {
        val tabLayout = binding.tabLayoutKategori
        tabLayout.removeAllTabs() // Bersihin tab default/dummy dulu

        // Tab pertama selalu HOME
        tabLayout.addTab(tabLayout.newTab().setText("HOME"))

        // Looping data kategori dari API
        for (kategori in kategoriList) {
            tabLayout.addTab(tabLayout.newTab().setText(kategori.namaKategori))
        }
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
                    setPadding(0, 0, 0, 24) // margin bottom
                }
                footerCategoryContainer.addView(textView)
            }
        }
    }
}