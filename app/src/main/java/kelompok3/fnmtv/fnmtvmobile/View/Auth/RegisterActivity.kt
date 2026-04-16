package kelompok3.fnmtv.fnmtvmobile.View.Auth

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kelompok3.fnmtv.fnmtvmobile.Database.Migration.DatabaseHelper
import kelompok3.fnmtv.fnmtvmobile.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsername = findViewById<EditText>(R.id.et_reg_username)
        val etEmail = findViewById<EditText>(R.id.et_reg_email)
        val etPassword = findViewById<EditText>(R.id.et_reg_password)
        val btnRegister = findViewById<Button>(R.id.btn_register)
        val tvKeLogin = findViewById<TextView>(R.id.tv_ke_login)

        val dbHelper = DatabaseHelper(this)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase

            // Cek apakah email sudah terdaftar
            val cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", arrayOf(email))
            if (cursor.moveToFirst()) {
                Toast.makeText(this, "Email sudah terdaftar! Silakan Login.", Toast.LENGTH_LONG).show()
                cursor.close()
                return@setOnClickListener
            }
            cursor.close()

            // Jika belum terdaftar, Insert data. Secara default user baru adalah "Viewer"
            val values = ContentValues().apply {
                put("username", username)
                put("email", email)
                put("password", password)
                put("role", "Viewer") // Sesuai default di database
                put("status", "Aktif")
            }

            val result = db.insert("users", null, values)
            if (result != -1L) {
                Toast.makeText(this, "Registrasi berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
                finish() // Kembali ke halaman Login
            } else {
                Toast.makeText(this, "Gagal mendaftar, coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }

        tvKeLogin.setOnClickListener {
            finish() // Langsung tutup activity ini biar balik ke tumpukan Login
        }
    }
}