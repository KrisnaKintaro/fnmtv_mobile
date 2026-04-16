package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Redaksi

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Redaksi.BeritaRedaksiAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Redaksi.VerifikasiBeritaController
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentMonitoringBeritaBinding

class MonitoringBeritaFragment : Fragment() {

    private var _binding: FragmentMonitoringBeritaBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var controller: VerifikasiBeritaController
    private lateinit var adapter: BeritaRedaksiAdapter
    private var listBerita: List<Berita> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBeritaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        controller = VerifikasiBeritaController(requireContext())
        setupRecyclerView()
        loadData()

        // Filter button actions
        binding.btnFilterSemua.setOnClickListener { filterData("Semua") }
        binding.btnFilterPending.setOnClickListener { filterData("Pending") }
        binding.btnFilterDitolak.setOnClickListener { filterData("Rejected") }
    }

    companion object {
        fun newInstance(tipe: String): MonitoringBeritaFragment {
            val fragment = MonitoringBeritaFragment()
            val args = Bundle()
            args.putString("TIPE_HALAMAN", tipe)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * FUNGSI setupRecyclerView(): Inisialisasi list berita dengan Adapter
     */
    private fun setupRecyclerView() {
        adapter = BeritaRedaksiAdapter(listOf()) { berita ->
            tampilkanDialogDetail(berita)
        }
        binding.rvMonitoringBerita.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMonitoringBerita.adapter = adapter
    }

    /**
     * FUNGSI loadData(): Mengambil data dari Controller dan menampilkan ke UI
     */
    private fun loadData() {
        val tipe = arguments?.getString("TIPE_HALAMAN") ?: "Antrean"

        listBerita = if (tipe == "Antrean") {
            controller.getBeritaForRedaksi() // Ambil Draft/Pending/Rejected
        } else {
            controller.getBeritaTerbit() // Ambil yang statusnya Published
        }

        adapter.updateData(listBerita)
    }

    /**
     * FUNGSI filterData(): Memfilter daftar berita berdasarkan status
     */
    private fun filterData(status: String) {
        val filtered = if (status == "Semua") {
            listBerita
        } else {
            listBerita.filter { it.status_berita == status }
        }
        adapter.updateData(filtered)
    }

    /**
     * FUNGSI tampilkanDialogDetail(): Memunculkan modal detail verifikasi berita
     * (Implementasi Point 3: DetailVerifikasiFragment dalam bentuk Dialog)
     */
    private fun tampilkanDialogDetail(berita: Berita) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_detail_verifikasi, null)
        val dialog = AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
            .setView(dialogView)
            .create()

        val etIsi = dialogView.findViewById<EditText>(R.id.etDetailIsi)
        val tvJudul = dialogView.findViewById<TextView>(R.id.tvDetailJudul)
        val tvMeta = dialogView.findViewById<TextView>(R.id.tvDetailMeta)
        val btnTerbitkan = dialogView.findViewById<Button>(R.id.btnTerbitkan)
        val btnTolak = dialogView.findViewById<Button>(R.id.btnTolak)

        tvJudul.text = berita.judul_berita
        tvMeta.text = "Editor: ${berita.nama_penulis} | Kategori: ${berita.nama_kategori} | Status: ${berita.status_berita}"
        etIsi.setText(berita.isi_berita)

        // Tombol Terbitkan: Langsung ubah status ke Published
        btnTerbitkan.setOnClickListener {
            val sukses = controller.updateStatusBerita(berita.id, "Published")
            if (sukses) {
                Toast.makeText(requireContext(), "Berita Berhasil Diterbitkan!", Toast.LENGTH_SHORT).show()
                loadData()
                dialog.dismiss()
            }
        }

        // Tombol Tolak: Munculkan dialog input catatan penolakan
        btnTolak.setOnClickListener {
            tampilkanDialogPenolakan(berita.id) {
                dialog.dismiss()
                loadData()
            }
        }

        dialog.show()
    }

    /**
     * FUNGSI tampilkanDialogPenolakan(): Mewajibkan Redaksi mengisi alasan penolakan
     * (Implementasi Point 5: dialog_penolakan.xml)
     */
    private fun tampilkanDialogPenolakan(idBerita: Int, onSuccess: () -> Unit) {
        val v = layoutInflater.inflate(R.layout.dialog_penolakan, null)
        val d = AlertDialog.Builder(requireContext()).setView(v).create()
        
        val etCatatan = v.findViewById<EditText>(R.id.etCatatanPenolakan)
        val btnKirim = v.findViewById<Button>(R.id.btnKirimPenolakan)
        val btnBatal = v.findViewById<Button>(R.id.btnBatalTolak)

        btnBatal.setOnClickListener { d.dismiss() }
        
        btnKirim.setOnClickListener {
            val catatan = etCatatan.text.toString().trim()
            if (catatan.isEmpty()) {
                etCatatan.error = "Alasan penolakan wajib diisi!"
                return@setOnClickListener
            }

            val sukses = controller.updateStatusBerita(idBerita, "Rejected", catatan)
            if (sukses) {
                Toast.makeText(requireContext(), "Berita dikembalikan ke Editor", Toast.LENGTH_SHORT).show()
                onSuccess()
                d.dismiss()
            }
        }
        d.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
