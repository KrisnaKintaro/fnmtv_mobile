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
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.SearchFragment
import kotlinx.coroutines.launch
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment.EditProfilFragment

class MasterViewersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterViewersBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterViewersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        sessionManager = SessionManager(this)
        kelolaKondisiNavbarUser()
        fetchKategoriDariApi()

        binding.btnSearchSubmit.setOnClickListener {
            val keyword = binding.etSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SearchFragment.newInstance(keyword))
                    .commit()
            } else {
                Toast.makeText(this, "Ketikkan kata kunci dulu!", Toast.LENGTH_SHORT).show()
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
                            // === KODE TRANSAKSI FRAGMENT MASUK DISINI ===
                            val editProfilFragment = EditProfilFragment()
                            supportFragmentManager.beginTransaction().apply {
                                replace(R.id.fragment_container, editProfilFragment)
                                addToBackStack(null)
                                commit()
                            }
                            true
                        }
                        "Keluar / Logout" -> {
                            Toast.makeText(this@MasterViewersActivity, "Sedang keluar...", Toast.LENGTH_SHORT).show()

                            lifecycleScope.launch {
                                try {
                                    ApiClient.getApiService(this@MasterViewersActivity).logoutUser()
                                } catch (e: Exception) {
                                    Log.e("LOGOUT_API", "Server gagal dihubungi")
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
            // bagian kondisi else (Login / Register) tetap sama
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
            try {
                val response = ApiClient.getApiService(this@MasterViewersActivity).getKategori()
                if (response.isSuccessful && response.body()?.status == "success") {
                    val daftarKategori = response.body()?.data ?: emptyList()
                    setupNavbar(daftarKategori)
                    setupFooter(daftarKategori)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Koneksi Error: ${e.message}")
            }
        }
    }

    private fun setupNavbar(kategoriList: List<KategoriItem>) {
        val tabLayout = binding.tabLayoutKategori
        tabLayout.removeAllTabs()

        tabLayout.addTab(tabLayout.newTab().setText("HOME"))

        for (kategori in kategoriList) {
            val tab = tabLayout.newTab().setText(kategori.namaKategori).setTag(kategori.id)
            tabLayout.addTab(tab)
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val transaction = supportFragmentManager.beginTransaction()

                if (tab?.text == "HOME") {
                    transaction.replace(R.id.fragment_container, HomeFragment())
                } else {
                    val idKategori = tab?.tag as? Int ?: 0
                    val namaKategori = tab?.text.toString()

                    transaction.replace(R.id.fragment_container, KategoriFragment.newInstance(idKategori.toString(), namaKategori))
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
            for (kategori in kategoriList.take(4)) {
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
