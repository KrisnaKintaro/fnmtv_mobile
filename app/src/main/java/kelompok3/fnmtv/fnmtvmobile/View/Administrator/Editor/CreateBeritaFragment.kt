package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Editor.EditorController
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentCreateBeritaBinding

class CreateBeritaFragment : Fragment() {

    private var _binding: FragmentCreateBeritaBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: EditorController
    private var editBeritaId: Int = -1

    companion object {
        fun newInstance(beritaId: Int = -1) = CreateBeritaFragment().apply {
            arguments = Bundle().apply { putInt("BERITA_ID", beritaId) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBeritaId = arguments?.getInt("BERITA_ID") ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateBeritaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = EditorController(requireContext())

        setupSpinner()

        binding.btnSimpanDraft.setOnClickListener {
            val judul = binding.etJudulBerita.text.toString()
            val isi = binding.etIsiBerita.text.toString()

            if (judul.isEmpty() || isi.isEmpty()) {
                Toast.makeText(context, "Judul dan Isi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val berita = Berita(
                user_id = 1, // Dummy ID
                kategori_id = 1, // Dummy
                judul_berita = judul,
                slug = judul.lowercase().replace(" ", "-"),
                isi_berita = isi,
                foto_thumbnail = "default.jpg",
                status_berita = "Draft"
            )

            if (controller.simpanSebagaiDraft(berita)) {
                Toast.makeText(context, "Berhasil simpan draft", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

        binding.btnKirimRedaksi.setOnClickListener {
            Toast.makeText(context, "Berita dikirim ke Redaksi!", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupSpinner() {
        val categories = arrayOf("Politik", "Ekonomi", "Teknologi", "Olahraga")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        binding.spinKategori.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
