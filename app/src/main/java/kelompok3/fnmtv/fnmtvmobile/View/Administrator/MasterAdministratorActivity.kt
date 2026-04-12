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
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityMasterAdministratorBinding // Nama otomatis dari XML lu

class MasterAdministratorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterAdministratorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterAdministratorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white_pure) // Pakai warna putih lu
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(firstFragment())
            binding.toolbar.title = "Analitik Statistik"
        }

        // REVISI 1 & 3: Perbaikan Navigasi Bawah
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_analisis -> {
                    replaceFragment(firstFragment())
                    binding.toolbar.title = "Analitik Statistik Berita"
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

        // REVISI 4: Menghidupkan Sidebar (Navigation View)
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_finance -> {
                    binding.toolbar.title = "Laporan Finansial"
                }
                R.id.nav_settings -> {
                    binding.toolbar.title = "Pengaturan"
                }
            }
            // Tutup sidebar otomatis setelah diklik
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // REVISI 5: Mantra khusus buat munculin icon di menu titik 3 (Overflow Menu)
    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (menu.javaClass.simpleName == "MenuBuilder") {
            try {
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

    // 9. Logika pas Menu Titik 3 diklik (Profil & Logout)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Balik ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish() // Tutup halaman admin biar gak bisa di-back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 10. Biar pas mencet tombol 'Back' di HP, Sidebar-nya ketutup dulu (gak langsung keluar aplikasi)
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}