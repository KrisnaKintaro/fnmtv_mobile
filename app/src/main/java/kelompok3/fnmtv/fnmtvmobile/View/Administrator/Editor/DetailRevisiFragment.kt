package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Editor.EditorController
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentCreateBeritaBinding

class DetailRevisiFragment : Fragment() {

    private var _binding: FragmentCreateBeritaBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: EditorController
    private var beritaId: Int = -1

    companion object {
        fun newInstance(id: Int) = DetailRevisiFragment().apply {
            arguments = Bundle().apply { putInt("BERITA_ID", id) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beritaId = arguments?.getInt("BERITA_ID") ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateBeritaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = EditorController(requireContext())

        // Setup UI untuk Mode Revisi
        binding.tvTitlePage.text = "Revisi Berita"
        binding.btnSimpanDraft.text = "Simpan Perubahan"
        binding.btnKirimRedaksi.text = "Kirim Ulang"

        loadDataBerita()

        binding.btnSimpanDraft.setOnClickListener {
            simpanRevisi()
        }

        binding.btnKirimRedaksi.setOnClickListener {
            if (simpanRevisi()) {
                if (controller.ajukanPublikasi(beritaId)) {
                    Toast.makeText(context, "Berita berhasil dikirim ulang!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun loadDataBerita() {
        val allBerita = controller.getBeritaSaya(1) // Dummy user ID
        val berita = allBerita.find { it.id == beritaId }
        
        berita?.let {
            binding.etJudulBerita.setText(it.judul_berita)
            binding.etIsiBerita.setText(it.isi_berita)
            
            // Tampilkan Catatan Revisi melalui ID include 'layout_revisi'
            if (!it.catatan_penolakan.isNullOrEmpty()) {
                binding.layoutRevisi.root.visibility = View.VISIBLE
                binding.layoutRevisi.txtCatatanRevisi.text = it.catatan_penolakan
            }
        }
    }

    private fun simpanRevisi(): Boolean {
        val judul = binding.etJudulBerita.text.toString()
        val isi = binding.etIsiBerita.text.toString()

        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(context, "Judul dan Isi wajib diisi!", Toast.LENGTH_SHORT).show()
            return false
        }

        return if (controller.revisiBerita(beritaId, judul, isi, "default.jpg")) {
            Toast.makeText(context, "Perubahan disimpan", Toast.LENGTH_SHORT).show()
            true
        } else {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
