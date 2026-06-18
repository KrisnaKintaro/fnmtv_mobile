package kelompok3.fnmtv.fnmtvmobile.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kelompok3.fnmtv.fnmtvmobile.data.local.SessionManager
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityLoginBinding
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.MasterViewersActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels() // Nyantol ke otak pengolah data
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // 1. Amati Variabel Loading (Kondisi tombol pas loading API)
        viewModel.isLoading.observe(this) { loading ->
            binding.btnLoginSubmit.isEnabled = !loading
            binding.btnLoginSubmit.text = if (loading) "Memproses..." else "Masuk"
        }

        viewModel.loginResult.observe(this) { response ->
            if (response != null && response.isSuccessful && response.body()?.status == "success") {
                val authData = response.body()

                if (authData?.redirect == "/email/verify") {
                    androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Verifikasi Diperlukan")
                        .setMessage("Akun anda belum diverifikasi! Cek email yang barusan didaftarin dan klik link verifikasinya.")
                        .setPositiveButton("Oke, Ngerti") { _, _ -> }
                        .show()
                    return@observe // BERHENTI DI SINI, jangan simpen token atau pindah layar!
                }

                val userData = authData?.data ?: authData?.user

                sessionManager.saveSession(
                    token = authData?.token ?: "",
                    userId = userData?.id ?: 0,
                    username = userData?.username ?: "Tanpa Nama",
                    email = userData?.email ?: ""
                )

                val roleUser = userData?.role
                Toast.makeText(this, "Login Berhasil, Role: $roleUser, Username:  ${userData?.username}", Toast.LENGTH_LONG).show()

                val intent = Intent(this, MasterViewersActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                val kodeError = response?.code()
                val pesanError = response?.errorBody()?.string()

                Log.e("API_LOGIN_ERROR", "Kode HTTP: $kodeError | Body: $pesanError")
                Toast.makeText(this, "Error $kodeError: Cek Logcat cuy!", Toast.LENGTH_LONG).show()
            }
        }

        // 3. Kejadian saat tombol Masuk diklik
        binding.btnLoginSubmit.setOnClickListener {
            val inputEmail = binding.etEmail.text.toString().trim()
            val inputPas = binding.etPassword.text.toString().trim()

            if (inputEmail.isEmpty() || inputPas.isEmpty()) {
                Toast.makeText(this, "Wajib diisi semua gank!", Toast.LENGTH_SHORT).show()
            } else {
                // Oper isi ketikan ke ViewModel buat diproses tembak ke internet
                viewModel.prosesLogin(inputEmail, inputPas)
            }
        }

        binding.btnBack.setOnClickListener {
            // Perintah finish() akan mematikan layar Login dan kembali ke layar sebelumnya (MasterViewersActivity)
            finish()
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.forgotPassResult.observe(this) { response ->
            if (response != null && response.isSuccessful && response.body()?.status == "success") {
                Toast.makeText(this, response.body()?.message ?: "Link reset terkirim ke email lu!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Gagal ngirim link! Pastikan email lu emang beneran terdaftar cuy.", Toast.LENGTH_LONG).show()
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            val viewDialog = layoutInflater.inflate(kelompok3.fnmtv.fnmtvmobile.R.layout.dialog_forgot_password, null)
            val etEmail = viewDialog.findViewById<com.google.android.material.textfield.TextInputEditText>(kelompok3.fnmtv.fnmtvmobile.R.id.etForgotEmail)

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setView(viewDialog)
                .setPositiveButton("Kirim Link") { _, _ ->
                    val email = etEmail.text.toString().trim()
                    if (email.isNotEmpty()) {
                        viewModel.prosesForgotPassword(email)
                    } else {
                        Toast.makeText(this, "Email nggak boleh kosong cuy!", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}
