package kelompok3.fnmtv.fnmtvmobile.View.Administrator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment // KOMPONEN NO 15
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentEditProfilBinding

class EditProfilFragment : Fragment() {

    private var _binding: FragmentEditProfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Logika saat KOMPONEN NO 3 (Button) ditekan
        binding.btnSimpanProfil.setOnClickListener {
            // Ambil data dari KOMPONEN NO 4 (EditText)
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val passLama = binding.etPasswordLama.text.toString()
            val passBaru = binding.etPasswordBaru.text.toString()

            // 1. Validasi Dasar
            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(context, "Username dan Email wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Berhenti di sini kalau kosong
            }

            // 2. Logika Ganti Password
            if (passBaru.isNotEmpty()) {
                // Kalau mau ganti password baru, password lama harus diisi buat keamanan
                if (passLama.isEmpty()) {
                    Toast.makeText(context, "Masukkan password lama untuk konfirmasi ganti password!", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // [NANTI DI SINI KITA MASUKIN QUERY KE KOMPONEN NO 17 (SQLite)]
                Toast.makeText(context, "Profil & Password $username berhasil diupdate!", Toast.LENGTH_SHORT).show()
            } else {
                // Kalau password baru kosong, berarti cuma update nama/email aja
                // [NANTI DI SINI KITA MASUKIN QUERY KE KOMPONEN NO 17 (SQLite)]
                Toast.makeText(context, "Profil $username berhasil diupdate!", Toast.LENGTH_SHORT).show()
            }

            // Kosongkan form password setelah berhasil disave
            binding.etPasswordLama.text.clear()
            binding.etPasswordBaru.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}