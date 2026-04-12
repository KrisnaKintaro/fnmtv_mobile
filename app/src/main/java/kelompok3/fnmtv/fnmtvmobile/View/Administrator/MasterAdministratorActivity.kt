package kelompok3.fnmtv.fnmtvmobile.View.Administrator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin.firstFragment
import kelompok3.fnmtv.fnmtvmobile.View.Auth.LoginActivity
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityMasterAdministratorBinding

class MasterAdministratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterAdministratorBinding
    private lateinit var toggle: ActionBarDrawerToggle // Jadikan global biar bisa diakses function lain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Setup dasar Hamburger Icon
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white_pure)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 1. Ambil Role
        var roleUser = intent.getStringExtra("ROLE_USER") ?: "Admin"
        roleUser = "Admin"

        // 2. Panggil Router yang Rapi (Nggak ada lagi if-else numpuk di sini)
        when (roleUser) {
            "Admin" -> setupRoleAdmin()
            "Editor" -> setupRoleEditor()
            "Redaksi" -> setupRoleRedaksi()
        }
    }

    private fun applyUIConfig(title: String, bottomMenuRes: Int, lockSidebar: Boolean) {
        // 1. Ganti Judul
        binding.toolbar.title = title

        // 2. Ganti Menu Bawah
        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(bottomMenuRes)

        // 3. Atur Kunci Sidebar
        if (lockSidebar) {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            toggle.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            toggle.isDrawerIndicatorEnabled = true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupRoleAdmin() {
        applyUIConfig("Analitik Statistik", R.menu.bottom_menu_admin, lockSidebar = false)
        replaceFragment(firstFragment()) // Fragment awal Admin

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_analisis -> {
                    binding.toolbar.title = "Analitik Statistik"
                    replaceFragment(firstFragment())
                }
                R.id.nav_users -> {
                    binding.toolbar.title = "Manajemen User"
                }
                R.id.nav_kategori -> {
                    binding.toolbar.title = "Manajemen Kategori"
                }
                R.id.nav_komentar -> {
                    binding.toolbar.title = "Moderasi Komentar"
                }
            }
            true
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_finance -> {
                    binding.toolbar.title = "Laporan Finansial"
                }
                R.id.nav_settings -> {
                    binding.toolbar.title = "Pengaturan"
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupRoleEditor() {
        applyUIConfig("Editor Panel", R.menu.bottom_menu_editor, lockSidebar = true)
        // replaceFragment(EditorDashboardFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tulis_berita -> {
                    binding.toolbar.title = "Tulis Berita Baru"
                }
                R.id.nav_berita_saya -> {
                    binding.toolbar.title = "Daftar Berita Saya"
                }
            }
            true
        }
    }

    private fun setupRoleRedaksi() {
        applyUIConfig("Panel Redaksi", R.menu.bottom_menu_redaksi, lockSidebar = true)
        // replaceFragment(RedaksiDashboardFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_verifikasi -> binding.toolbar.title = "Antrean Verifikasi"
                R.id.nav_berita_terbit -> binding.toolbar.title = "Riwayat Publikasi"
            }
            true
        }
    }

    // REVISI 5: Mantra khusus buat munculin icon di menu titik 3 (Overflow Menu)
    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
                // Menggunakan teknik 'Reflection' buat ngakses method tersembunyi milik Android
                val m = menu.javaClass.getDeclaredMethod("setOptionalIconsVisible", java.lang.Boolean.TYPE)
                m.isAccessible = true
                m.invoke(menu, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onMenuOpened(featureId, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_admin, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}