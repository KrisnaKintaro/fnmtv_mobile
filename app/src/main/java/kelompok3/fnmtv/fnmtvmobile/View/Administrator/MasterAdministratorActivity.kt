package kelompok3.fnmtv.fnmtvmobile.View.Administrator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin.manajemenKategoriFragment
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin.manajemenUserFragment
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin.statistikBeritaFragment
import kelompok3.fnmtv.fnmtvmobile.View.Auth.LoginActivity
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityMasterAdministratorBinding
import androidx.appcompat.app.AppCompatDelegate
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor.CreateBeritaFragment
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor.DetailRevisiFragment
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor.ListDraftFragment

import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Redaksi.MonitoringBeritaFragment

class MasterAdministratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterAdministratorBinding
    private lateinit var toggle: ActionBarDrawerToggle // dibuat global agar bisa diakses fungsi`    lain

    override fun onCreate(savedInstanceState: Bundle?) {
        // Disable night mode ngeselin
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        binding = ActivityMasterAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Munculin Hamburger
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Nyiapin Lahan: Baris ini ngasih "lahan" di pojok kiri atas navbar lu. dan
        // Dibajak sama Hamburger: Karena lu udah jalanin toggle.syncState() di baris sebelumnya, lahan yang tadinya mau diisi panah back itu langsung di-replace (dibajak) wujudnya jadi icon Hamburger (garis tiga).
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Role
        val sharedPref = getSharedPreferences("SESSION_FNMTV", MODE_PRIVATE)
        val roleUser = sharedPref.getString("USER_ROLE", "Viewer") ?: "Viewer"
        // roleUser = "Admin"

        // 2. Panggil Router yang Rapi (Nggak ada lagi if-else numpuk di sini)
        when (roleUser) {
            "Admin" -> setupRoleAdmin()
            "Editor" -> setupRoleEditor()
            "Redaksi" -> setupRoleRedaksi()
        }
    }

    private fun applyUIConfig(title: String, bottomMenuRes: Int, lockSidebar: Boolean) {
        // Ganti judul pakai supportActionBar
        supportActionBar?.title = title

        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(bottomMenuRes)

        if (lockSidebar) {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false) // Sembunyikan tombol back/hamburger
        } else {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    // Klik menu di titik 3 (option menu)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 1. Ini WAJIB buat nangkep klik icon garis 3 (Hamburger)
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        // 2. Nangkep menu umum
        return when (item.itemId) {
            R.id.action_profile -> {
                supportActionBar?.title = "Edit Profil"
                // replaceFragment(EditProfilFragment())
                true
            }

            R.id.action_logout -> {
                // --- HANCURKAN SESI LOGIN DI SINI ---
                val sharedPref = getSharedPreferences("SESSION_FNMTV", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    clear() // Bersihin semua data (IS_LOGGED_IN, USER_ROLE, dll)
                    apply()
                }

                // Kasih notif ke user
                Toast.makeText(this, "Berhasil Logout!", Toast.LENGTH_SHORT).show()

                // Baru lempar balik ke Login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> {
                // Kalau yang diklik BUKAN Profile atau Logout (misal: Refresh),
                // lempar ke Fragment yang lagi aktif di layar.
                super.onOptionsItemSelected(item)
            }
        }
    }

    // munculin titik 3 kanan atas navbar header
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_admin, menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupRoleAdmin() {
        applyUIConfig("Analitik Statistik", R.menu.bottom_menu_admin, lockSidebar = false)

        // Setup awal masuk tampilan awal
        replaceFragment(statistikBeritaFragment()) // Fragment awal Admin
        binding.bottomNav.selectedItemId = R.id.nav_analisis // Aktifin menu statistik berita di navbar bawah

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_analisis -> {
                    supportActionBar?.title = "Analitik Statistik"
                    replaceFragment(statistikBeritaFragment())
                }

                R.id.nav_users -> {
                    supportActionBar?.title = "Manajemen User"
                    replaceFragment(manajemenUserFragment())
                }

                R.id.nav_kategori -> {
                    supportActionBar?.title = "Manajemen Kategori"
                    replaceFragment(manajemenKategoriFragment())

                }

                R.id.nav_komentar -> {
                    supportActionBar?.title = "Moderasi Komentar"

                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_finance -> {
                    supportActionBar?.title = "Laporan Finansial"

                }

                R.id.nav_settings -> {
                    supportActionBar?.title = "Pengaturan"
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRoleEditor() {
        applyUIConfig("Daftar Berita Saya", R.menu.bottom_menu_editor, lockSidebar = true)
        
        // Tampilkan List Berita sebagai halaman awal Editor
        replaceFragment(ListDraftFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tulis_berita -> {
                    supportActionBar?.title = "Tulis Berita Baru"
                    replaceFragment(CreateBeritaFragment())
                }

                R.id.nav_berita_saya -> {
                    supportActionBar?.title = "Daftar Berita Saya"
                    replaceFragment(ListDraftFragment())
                }
            }
            true
        }
    }

    private fun setupRoleRedaksi() {
        applyUIConfig("Antrean Verifikasi", R.menu.bottom_menu_redaksi, lockSidebar = true)

        replaceFragment(MonitoringBeritaFragment.newInstance("Antrean"))

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_verifikasi -> {
                    supportActionBar?.title = "Antrean Verifikasi"
                    replaceFragment(MonitoringBeritaFragment.newInstance("Antrean"))

                }

                R.id.nav_berita_terbit -> {
                    supportActionBar?.title = "Riwayat Publikasi"
                    replaceFragment(MonitoringBeritaFragment.newInstance("Terbit"))
                }
            }
            true
        }
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                // Menggunakan teknik 'Reflection' buat ngakses method tersembunyi milik Android
                val m = menu.javaClass.getDeclaredMethod(
                    "setOptionalIconsVisible",
                    java.lang.Boolean.TYPE
                )
                m.isAccessible = true
                m.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
