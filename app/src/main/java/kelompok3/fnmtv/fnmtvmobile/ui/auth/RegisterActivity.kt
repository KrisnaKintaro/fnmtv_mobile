package kelompok3.fnmtv.fnmtvmobile.ui.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        viewModel.isLoading.observe(this) { loading ->
            binding.btnRegisterSubmit.isEnabled = !loading
            binding.btnRegisterSubmit.text = if (loading) "Memproses..." else "Daftar Sekarang"
        }

        viewModel.registerResult.observe(this) { response ->
            if (response != null && response.isSuccessful && response.body()?.status == "success") {
                // Tampilkan pesan sukses dan suruh cek email!
                AlertDialog.Builder(this)
                    .setTitle("Registrasi Berhasil!")
                    .setMessage(response.body()?.message ?: "Silakan cek kotak masuk email anda untuk verifikasi sebelum login.")
                    .setCancelable(false)
                    .setPositiveButton("Ke Halaman Login") { _, _ ->
                        finish() // Balik ke halaman login
                    }
                    .show()
            } else {
                val pesanError = response?.errorBody()?.string() ?: "Terjadi kesalahan"
                Toast.makeText(this, "Gagal Daftar! Pastikan email belum dipakai dan password minimal 8 huruf.", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnRegisterSubmit.setOnClickListener {
            val username = binding.etRegUsername.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val pas = binding.etRegPassword.text.toString().trim()
            val pasConf = binding.etRegPasswordConfirm.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || pas.isEmpty() || pasConf.isEmpty()) {
                Toast.makeText(this, "Isi semua formnya cuy!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pas != pasConf) {
                Toast.makeText(this, "Konfirmasi sandi nggak sama!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.prosesRegister(username, email, pas, pasConf)
        }
    }
}