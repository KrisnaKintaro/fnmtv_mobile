package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kelompok3.fnmtv.fnmtvmobile.data.local.SessionManager
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentEditProfilBinding
import kotlinx.coroutines.launch
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient

class EditProfilFragment : Fragment() {

    private var _binding: FragmentEditProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())

        setupToolbar()
        loadDataProfilLama()
        setupActionListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadDataProfilLama() {
        val namaLama = sessionManager.fetchUsername() ?: ""
        val emailLama = sessionManager.fetchEmail() ?: ""

        binding.tvNamaUser.text = namaLama.ifEmpty { "User FNMTV" }
        binding.tvEmailUser.text = emailLama.ifEmpty { "user@fnmtv.com" }

        binding.etUsername.setText(namaLama)
        binding.etEmail.setText(emailLama)
        binding.etEmail.isEnabled = false       // Mematikan fungsi input/klik
        binding.etEmail.isFocusable = false     // Menghilangkan fokus keyboard
        binding.etEmail.isFocusableInTouchMode = false
    }

    private fun setupActionListeners() {
        binding.btnGantiFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Fitur ganti foto profil segera hadir!", Toast.LENGTH_SHORT).show()
        }

        binding.btnSimpan.setOnClickListener {
            validasiDanKirimKeApi()
        }

        binding.btnBatal.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun validasiDanKirimKeApi() {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pwdLama = binding.etPasswordLama.text.toString()
        val pwdBaru = binding.etPasswordBaru.text.toString()
        val konfPwd = binding.etKonfirmasiPassword.text.toString()

        if (username.isEmpty()) {
            binding.etUsername.error = "Username tidak boleh kosong"
            binding.etUsername.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email tidak boleh kosong"
            binding.etEmail.requestFocus()
            return
        }

        // Validasi logika jika form ganti password diotak-atik
        if (pwdLama.isNotEmpty() || pwdBaru.isNotEmpty() || konfPwd.isNotEmpty()) {
            if (pwdLama.isEmpty()) {
                binding.etPasswordLama.error = "Masukkan password lama Anda"
                binding.etPasswordLama.requestFocus()
                return
            }
            if (pwdBaru.length < 8) {
                binding.etPasswordBaru.error = "Password baru minimal 8 karakter"
                binding.etPasswordBaru.requestFocus()
                return
            }
            if (pwdBaru != konfPwd) {
                binding.etKonfirmasiPassword.error = "Konfirmasi password tidak cocok"
                binding.etKonfirmasiPassword.requestFocus()
                return
            }
        }

        eksekusiUpdateProfil(username, email, pwdLama, pwdBaru, konfPwd)
    }

    private fun eksekusiUpdateProfil(username: String, email: String, pasLama: String, pasBaru: String, konfPas: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSimpan.isEnabled = false

        val tokenRaw = sessionManager.fetchAuthToken()
        if (tokenRaw.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesi Anda habis, silakan login kembali", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSimpan.isEnabled = true
            return
        }

        // ✅ PENANGANAN 401: Cek apakah token sudah menyimpan kata "Bearer " atau belum secara otomatis
        val tokenBearer = when {
            tokenRaw.startsWith("Bearer ", ignoreCase = true) -> tokenRaw
            else -> "Bearer $tokenRaw"
        }

        Log.d("FNMTV_DEBUG", "Menembak API dengan Token: $tokenBearer")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiClient.getApiService(requireContext())

                // Panggil interface ApiService murni PUT
                val response = apiService.updateProfilViewer(
                    token = tokenBearer,
                    username = username,
                    email = email,
                    currentPassword = pasLama,
                    newPassword = pasBaru,
                    konfirmasi = konfPas
                )

                if (response.isSuccessful) {
                    // Berhasil! Sinkronisasikan data ke Session lokal
                    sessionManager.saveUsername(username)
                    sessionManager.saveEmail(email)

                    Toast.makeText(requireContext(), "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    Log.e("API_UPDATE_ERROR", "Code: ${response.code()}, Message: $errorMsg")

                    // Variasi penanganan pesan berdasarkan status code response backend
                    when (response.code()) {
                        401 -> Toast.makeText(requireContext(), "Sesi login tidak valid (401). Coba log out lalu login kembali.", Toast.LENGTH_LONG).show()
                        422 -> Toast.makeText(requireContext(), "Gagal: Validasi ditolak server atau password lama salah.", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(requireContext(), "Gagal memperbarui profil (Kode: ${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("API_UPDATE_PROFIL", "Crash Error: ${e.message}")
                Toast.makeText(requireContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSimpan.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}