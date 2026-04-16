package kelompok3.fnmtv.fnmtvmobile.View.Viewers.Konten

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Adapter.Viewers.KomentarAdapter
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Komentar
import kelompok3.fnmtv.fnmtvmobile.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailBeritaFragment : Fragment() {

    private lateinit var etKomentar: EditText
    private lateinit var btnKirimKomentar: Button
    private lateinit var rvKomentar: RecyclerView
    private lateinit var adapter: KomentarAdapter
    private val listKomentar = mutableListOf<Komentar>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail_berita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi View
        etKomentar = view.findViewById(R.id.etKomentar)
        btnKirimKomentar = view.findViewById(R.id.btnKirimKomentar)
        rvKomentar = view.findViewById(R.id.rvKomentar)

        setupReaksi(view)
        setupKomentar()
    }

    private fun setupReaksi(view: View) {
        val reaksiLike: TextView = view.findViewById(R.id.reaksiLike)
        val reaksiLove: TextView = view.findViewById(R.id.reaksiLove)
        val reaksiWow: TextView = view.findViewById(R.id.reaksiWow)
        val reaksiSad: TextView = view.findViewById(R.id.reaksiSad)

        val listener = View.OnClickListener { v ->
            val jenis = when (v.id) {
                R.id.reaksiLike -> "Like 👍"
                R.id.reaksiLove -> "Love ❤️"
                R.id.reaksiWow -> "Wow 😯"
                R.id.reaksiSad -> "Sad 😢"
                else -> ""
            }
            Toast.makeText(context, "Anda memberikan reaksi: $jenis", Toast.LENGTH_SHORT).show()
        }

        reaksiLike.setOnClickListener(listener)
        reaksiLove.setOnClickListener(listener)
        reaksiWow.setOnClickListener(listener)
        reaksiSad.setOnClickListener(listener)
    }

    private fun setupKomentar() {
        // Data Dummy Komentar Awal
        listKomentar.add(Komentar(berita_id = 1, user_id = 0, nama_user = "Anonim", isi_komentar = "Wah beritanya sangat informatif!", created_at = "10 menit yang lalu"))
        listKomentar.add(Komentar(berita_id = 1, user_id = 0, nama_user = "Pengamat", isi_komentar = "Saya setuju dengan artikel ini.", created_at = "1 jam yang lalu"))

        adapter = KomentarAdapter(listKomentar)
        rvKomentar.layoutManager = LinearLayoutManager(context)
        rvKomentar.adapter = adapter

        btnKirimKomentar.setOnClickListener {
            val isi = etKomentar.text.toString().trim()
            if (isi.isNotEmpty()) {
                tambahKomentarBaru(isi)
            } else {
                Toast.makeText(context, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tambahKomentarBaru(isi: String) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val tanggalSekarang = sdf.format(Date())

        val komentarBaru = Komentar(
            berita_id = 1,
            user_id = 0, // 0 untuk guest/anonim
            nama_user = "Anda (Anonim)",
            isi_komentar = isi,
            created_at = tanggalSekarang
        )

        listKomentar.add(0, komentarBaru) // Tambah di paling atas
        adapter.notifyItemInserted(0)
        rvKomentar.scrollToPosition(0)
        
        etKomentar.setText("")
        Toast.makeText(context, "Komentar berhasil dikirim!", Toast.LENGTH_SHORT).show()
    }
}
