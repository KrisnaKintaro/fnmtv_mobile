package kelompok3.fnmtv.fnmtvmobile.View.Auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.View.Administrator.MasterAdministratorActivity
import kelompok3.fnmtv.fnmtvmobile.View.Viewers.MasterViewersActivity
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Paksa Light Mode
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)

        //  Masang seeder
        val seeder = kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseSeeder(this)
        seeder.run()

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", arrayOf(email, password))

            if (cursor.moveToFirst()) {
                val status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                val role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
                val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
                val userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"))

                if (status == "Nonaktif") {
                    Toast.makeText(this, "Akun Anda dinonaktifkan Admin!", Toast.LENGTH_LONG).show()
                } else {
                    // Simpan sesi ke SharedPreferences
                    val sharedPref = getSharedPreferences("SESSION_FNMTV", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("IS_LOGGED_IN", true)
                        putInt("USER_ID", userId)
                        putString("USER_ROLE", role)
                        putString("USERNAME", username)
                        apply()
                    }

                    Toast.makeText(this, "Selamat datang, $username!", Toast.LENGTH_SHORT).show()

                    // Routing
                    if (role == "Viewer") {
                        // Kalau masyarakat biasa (hasil Register), lempar ke halaman Viewers
                        val intent = Intent(this, MasterViewersActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Kalau tim internal (Admin, Editor, Redaksi), lempar ke Dashboard Admin
                        val intent = Intent(this, MasterAdministratorActivity::class.java)
                        startActivity(intent)
                    }
                    finish() // Tutup login activity
                }
            } else {
                Toast.makeText(this, "Email atau Password salah!", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        binding.tvKeRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvLupaPassword.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }
    }
}