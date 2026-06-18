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

        // ✅ SEKARANG PARAMETERNYA SUDAH COCOK 5 VALUE
        eksekusiUpdateProfil(username, email, pwdLama, pwdBaru, konfPwd)
    }

    // ✅ DEKLARASI JUGA MENERIMA 5 VALUE (username, email, pasLama, pasBaru, konfPas)
    private fun eksekusiUpdateProfil(username: String, email: String, pasLama: String, pasBaru: String, konfPas: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSimpan.isEnabled = false

        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Sesi Anda habis, silakan login kembali", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
            binding.btnSimpan.isEnabled = true
            return
        }

        val tokenBearer = if (!token.startsWith("Bearer ")) "Bearer $token" else token

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val apiService = ApiClient.getApiService(requireContext())

                val response = apiService.updateProfilViewer(
                    token = tokenBearer,
                    method = "PUT",
                    username = username,
                    email = email,
                    currentPassword = pasLama,
                    newPassword = pasBaru,
                    konfirmasi = konfPas
                )

                if (response.isSuccessful) {
                    sessionManager.saveUsername(username)
                    sessionManager.saveEmail(email)

                    Toast.makeText(requireContext(), "Profil & Password berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    Log.e("API_UPDATE_ERROR", "Code: ${response.code()}, Message: $errorMsg")

                    if (response.code() == 422) {
                        Toast.makeText(requireContext(), "Gagal: Data tidak valid atau password lama salah.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memperbarui profil (Kode: ${response.code()})", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("API_UPDATE_PROFIL", "Error: ${e.message}")
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