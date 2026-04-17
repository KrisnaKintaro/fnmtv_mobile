package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Editor.EditorController
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentCreateBeritaBinding
import java.io.File
import java.io.FileOutputStream

class CreateBeritaFragment : Fragment() {

    private var _binding: FragmentCreateBeritaBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: EditorController
    private var editBeritaId: Int = -1
    private var currentUserId: Int = -1

    // Simpan path permanen
    private var savedImagePath: String = "default.jpg"

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    // Copy ke internal storage supaya path permanen
                    val path = copyImageToInternalStorage(uri)
                    if (path != null) {
                        savedImagePath = path
                        binding.imgPreviewThumbnail.setImageURI(Uri.fromFile(File(path)))
                    } else {
                        Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    companion object {
        fun newInstance(beritaId: Int = -1) = CreateBeritaFragment().apply {
            arguments = Bundle().apply { putInt("BERITA_ID", beritaId) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBeritaId = arguments?.getInt("BERITA_ID") ?: -1
        val sharedPref = requireContext().getSharedPreferences("SESSION_FNMTV", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getInt("USER_ID", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBeritaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = EditorController(requireContext())
        setupSpinner()

        if (editBeritaId != -1) {
            loadDataBeritaLama()
        } else {
            binding.tvTitlePage.text = "Tulis Berita Baru"
            binding.layoutRevisi.root.visibility = View.GONE
        }

        binding.btnPilihFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.btnSimpanDraft.setOnClickListener { simpanBeritaProses("Draft") }
        binding.btnKirimRedaksi.setOnClickListener { simpanBeritaProses("Pending") }
    }

    // Copy gambar dari galeri ke folder internal app.
    // Menampilkan path file
    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            // Buat folder thumbnails di internal storage
            val dir = File(requireContext().filesDir, "thumbnails")
            if (!dir.exists()) dir.mkdirs()
            // Nama file unik berdasarkan timestamp
            val fileName = "thumb_${System.currentTimeMillis()}.jpg"
            val destFile = File(dir, fileName)
            FileOutputStream(destFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            destFile.absolutePath // Return path absolut
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadDataBeritaLama() {
        val berita = controller.getBeritaSaya(currentUserId).find { it.id == editBeritaId }
        berita?.let {
            binding.etJudulBerita.setText(it.judul_berita)
            binding.etIsiBerita.setText(it.isi_berita)

            // Load gambar dari path yang tersimpan di database
            savedImagePath = it.foto_thumbnail ?: "default.jpg"
            if (savedImagePath != "default.jpg" && File(savedImagePath).exists()) {
                binding.imgPreviewThumbnail.setImageURI(Uri.fromFile(File(savedImagePath)))
            }

            when (it.status_berita) {
                "Pending", "Published" -> {
                    binding.tvTitlePage.text = "Detail Berita (${it.status_berita})"
                    binding.layoutRevisi.root.visibility = View.GONE
                    disableEditing()
                }
                "Rejected" -> {
                    binding.tvTitlePage.text = "Revisi Berita"
                    binding.btnSimpanDraft.text = "Simpan Perubahan"
                    binding.btnKirimRedaksi.text = "Kirim Ulang ke Redaksi"
                    if (!it.catatan_penolakan.isNullOrEmpty()) {
                        binding.layoutRevisi.root.visibility = View.VISIBLE
                        binding.layoutRevisi.txtCatatanRevisi.text = it.catatan_penolakan
                    } else {
                        binding.layoutRevisi.root.visibility = View.GONE
                    }
                }
                "Draft" -> {
                    binding.tvTitlePage.text = "Edit Draft"
                    binding.layoutRevisi.root.visibility = View.GONE
                }
                else -> {
                    binding.tvTitlePage.text = "Detail Berita"
                    binding.layoutRevisi.root.visibility = View.GONE
                }
            }
        }
    }

    private fun disableEditing() {
        binding.etJudulBerita.isEnabled = false
        binding.etIsiBerita.isEnabled = false
        binding.spinKategori.isEnabled = false
        binding.btnPilihFoto.visibility = View.GONE
        binding.btnSimpanDraft.visibility = View.GONE
        binding.btnKirimRedaksi.visibility = View.GONE
    }

    private fun simpanBeritaProses(status: String) {
        val judul = binding.etJudulBerita.text.toString().trim()
        val isi = binding.etIsiBerita.text.toString().trim()

        if (judul.isEmpty() || isi.isEmpty()) {
            Toast.makeText(context, "Judul dan Isi wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentUserId == -1) {
            Toast.makeText(context, "Sesi user tidak ditemukan, silakan login ulang!", Toast.LENGTH_SHORT).show()
            return
        }

        val berita = Berita(
            id = if (editBeritaId != -1) editBeritaId else 0,
            user_id = currentUserId,
            kategori_id = 1,
            judul_berita = judul,
            slug = judul.lowercase().replace(" ", "-"),
            isi_berita = isi,
            foto_thumbnail = savedImagePath, // Pakai path permanen
            status_berita = status
        )

        val sukses: Boolean = if (editBeritaId == -1) {
            controller.simpanBerita(berita)
        } else {
            val updContent = controller.revisiBerita(editBeritaId, judul, isi, savedImagePath)
            val updStatus = controller.updateStatusBerita(editBeritaId, status)
            updContent && updStatus
        }

        if (sukses) {
            Toast.makeText(context, "Berita berhasil diproses sebagai $status", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        } else {
            Toast.makeText(context, "Gagal menyimpan ke database!", Toast.LENGTH_SHORT).show()
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