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
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityLupaPasswordBinding

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLupaPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbHelper = DatabaseHelper(this)

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etLupaEmail.text.toString().trim()
            val passwordBaru = binding.etLupaPasswordBaru.text.toString().trim()

            if (email.isEmpty() || passwordBaru.isEmpty()) {
                Toast.makeText(this, "Email dan Password Baru wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = dbHelper.writableDatabase

            // Cek apakah emailnya ada di database
            val cursor = db.rawQuery("SELECT id FROM users WHERE email = ?", arrayOf(email))
            if (cursor.moveToFirst()) {
                // Email ditemukan, lakukan update password
                val values = ContentValues().apply {
                    put("password", passwordBaru)
                }

                db.update("users", values, "email = ?", arrayOf(email))
                Toast.makeText(this, "Password berhasil di-reset! Silakan login.", Toast.LENGTH_LONG).show()
                finish() // Balik ke halaman login
            } else {
                Toast.makeText(this, "Email tidak terdaftar di sistem kami!", Toast.LENGTH_SHORT).show()
            }
            cursor.close()
        }

        binding.tvKembaliLogin.setOnClickListener {
            finish()
        }
    }
}