package kelompok3.fnmtv.fnmtvmobile.View.Viewers.Konten

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Adapter.Viewers.BeritaAdapter
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.View.Viewers.MasterViewersActivity

class HomeFragment : Fragment() {

    private lateinit var rvTrending: RecyclerView
    private lateinit var rvTerbaru: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTrending = view.findViewById(R.id.rvTrending)
        rvTerbaru = view.findViewById(R.id.rvTerbaru)

        setupDummyData()
    }

    private fun setupDummyData() {
        val dummyList = listOf(
            Berita(
                id = 1,
                user_id = 1,
                kategori_id = 1,
                judul_berita = "VERY deeply with a pair of white kid gloves, and.",
                slug = "berita-1",
                isi_berita = "Isi berita lengkap...",
                foto_thumbnail = "",
                nama_kategori = "NASIONAL - HIBURAN",
                waktu_publikasi = "23 April 2024 pukul 14:50 WIB"
            )
        )

        val adapter = BeritaAdapter(dummyList) { berita ->
            // Navigasi ke DetailBeritaFragment
            (activity as? MasterViewersActivity)?.loadFragment(DetailBeritaFragment())
        }

        rvTrending.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTrending.adapter = adapter

        rvTerbaru.layoutManager = LinearLayoutManager(context)
        rvTerbaru.adapter = adapter
    }
}
