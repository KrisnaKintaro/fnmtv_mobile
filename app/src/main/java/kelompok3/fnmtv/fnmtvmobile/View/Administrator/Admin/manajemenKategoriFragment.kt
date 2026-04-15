package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin.manajemenKategoriAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin.KategoriController
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Kategori
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentManajemenKategoriBinding

class manajemenKategoriFragment : Fragment() {

    private var _binding: FragmentManajemenKategoriBinding? = null
    private val binding get() = _binding!!

    private lateinit var kategoriController: KategoriController
    private lateinit var adapter: manajemenKategoriAdapter
    private var semuaKategori: List<Kategori> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManajemenKategoriBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true) // Wajib biar bisa nangkep klik icon Refresh dari Master Activity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kategoriController = KategoriController(requireContext())
        loadDataDariDatabase()

        // Live Search Kategori
        binding.etSearchKategori.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString().lowercase()
                val hasilPencarian = semuaKategori.filter {
                    it.nama_kategori.lowercase().contains(keyword)
                }
                adapter.updateData(hasilPencarian)
            }
        })

        // Tombol Tambah
        binding.btnTambahKategori.setOnClickListener {
            tampilkanDialogTambahEdit(null) // null berarti mode Tambah
        }
    }

    private fun loadDataDariDatabase() {
        semuaKategori = kategoriController.getAllKategori()

        adapter = manajemenKategoriAdapter(
            context = requireContext(),
            kategoris = semuaKategori,
            onEditClick = { kategori -> tampilkanDialogTambahEdit(kategori) },
            onDeleteClick = { kategori -> tampilkanDialogKonfirmasiHapus(kategori) }
        )
        binding.lvKategori.adapter = adapter
    }

    private fun tampilkanDialogTambahEdit(kategori: Kategori?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_edit_kategori, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etNamaKategori = dialogView.findViewById<EditText>(R.id.et_form_nama_kategori)
        val etSlugKategori =
            dialogView.findViewById<EditText>(R.id.et_form_slug_kategori) // Ambil ID Slug
        val btnBatal = dialogView.findViewById<Button>(R.id.btn_batal_kategori)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btn_simpan_kategori)

        // LOGIKA AUTO-SLUG
        var isAutoGenerating = true

        etNamaKategori.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isAutoGenerating) {
                    val autoSlug = s.toString().lowercase()
                        .replace(
                            Regex("[^a-z0-9\\s-]"),
                            ""
                        ) // Buang karakter spesial kecuali spasi & strip
                        .trim()
                        .replace(Regex("\\s+"), "-") // Ganti spasi jadi strip

                    etSlugKategori.setText(autoSlug)
                }
            }
        })

        // Kalau user iseng ngetik manual di kolom Slug, matikan auto-generate sementara
        // Biar ketikan manual dia gak ketimpa sama Nama Kategori lagi
        etSlugKategori.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isAutoGenerating = false
        }

        // Cek Mode (Edit atau Tambah)
        if (kategori != null) {
            tvTitle.text = "Edit Kategori"
            etNamaKategori.setText(kategori.nama_kategori)
            etSlugKategori.setText(kategori.slug) // Tampilkan slug lama
        } else {
            tvTitle.text = "Tambah Kategori"
        }

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val inputNama = etNamaKategori.text.toString().trim()
            val inputSlug = etSlugKategori.text.toString().trim()

            if (inputNama.isEmpty() || inputSlug.isEmpty()) {
                Toast.makeText(requireContext(), "Semua field wajib diisi cuy!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (kategori == null) {
                // Proses tambah
                val newKategori = Kategori(0, inputNama, inputSlug)
                if (kategoriController.tambahKategori(newKategori)) {
                    Toast.makeText(
                        requireContext(),
                        "Kategori berhasil ditambahkan!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            } else {
                // Proses Edit
                val updatedKategori = Kategori(kategori.id, inputNama, inputSlug)
                if (kategoriController.editKategori(updatedKategori)) {
                    Toast.makeText(
                        requireContext(),
                        "Kategori berhasil diupdate!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    // --- FORM KONFIRMASI HAPUS ---
    private fun tampilkanDialogKonfirmasiHapus(kategori: Kategori) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi_hapus_kategori, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.tv_hapus_nama_kategori).text = kategori.nama_kategori

        dialogView.findViewById<Button>(R.id.btn_batal_hapus_kategori)
            .setOnClickListener { dialog.dismiss() }

        dialogView.findViewById<Button>(R.id.btn_konfirmasi_hapus_kategori).setOnClickListener {
            if (kategoriController.hapusKategori(kategori.id)) {
                Toast.makeText(requireContext(), "Kategori musnah!", Toast.LENGTH_SHORT).show()
                loadDataDariDatabase()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    // --- TERIMA LEMPARAN REFRESH DARI MASTER ACTIVITY ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            loadDataDariDatabase()
            Toast.makeText(context, "Data kategori disegarkan!", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}