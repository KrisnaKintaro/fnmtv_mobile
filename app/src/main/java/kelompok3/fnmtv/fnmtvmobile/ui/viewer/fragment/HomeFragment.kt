package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentHomeBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.BeritaAdapter
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.TrendingAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Karena layout hero di-include, kita tetep bisa panggil ID-nya asalkan inflate-nya bener
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mulai tarik data dari server!
        fetchDataHome()
    }

    private fun fetchDataHome() {
        viewLifecycleOwner.lifecycleScope.launch {
            var isSuccess = false

            while (!isSuccess) {
                try {
                    val response = ApiClient.getApiService(requireContext()).getSemuaBerita()

                    if (response.isSuccessful && response.body()?.status == "success") {
                        val dataBerita = response.body()?.data

                        dataBerita?.headline?.let { renderHeroSection(it) }
                        dataBerita?.terbaru?.let { renderBeritaTerbaru(it) }
                        dataBerita?.trending?.let { renderTrending(it) }

                        isSuccess = true
                    } else {
                        Log.e("API_RETRY", "Gagal dapet berita, mencoba lagi dalam 2 detik...")
                        kotlinx.coroutines.delay(2000)
                    }
                } catch (e: Exception) {
                    Log.e("API_CRASH", "Pesan Error: ${e.message}. Mencoba lagi dalam 2 detik...")
                    kotlinx.coroutines.delay(2000)
                }
            }
        }
    }

    private fun renderBeritaTerbaru(terbaruList: List<BeritaItem>) {
        if (terbaruList.isEmpty()) return

        val adapter = BeritaAdapter(terbaruList)

        // PENTING: LayoutManager ini ibarat ngasih tau RecyclerView "Tolong susun card-nya ke BAWAH ya"
        binding.rvBeritaTerbaru.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeritaTerbaru.adapter = adapter
    }

    private fun renderTrending(trendingList: List<BeritaItem>) {
        if (trendingList.isEmpty()) return

        val adapter = TrendingAdapter(trendingList)

        binding.rvTrending.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTrending.adapter = adapter
    }

    private fun renderHeroSection(headlineList: List<BeritaItem>) {
        // Kalau ternyata API ngirim headline kosong (Belum diset Admin)
        if (headlineList.isEmpty()) {
            binding.heroSection.tvHeroMainTitle.text = "Belum Ada Headline Utama"
            binding.heroSection.tvHeroMainCat.text = "INFO"
            return
        }

        // -- HERO MAIN (Berita 1 / Terbesar) --
        val mainNews = headlineList[0]
        binding.heroSection.tvHeroMainTitle.text = mainNews.judulBerita
        binding.heroSection.tvHeroMainCat.text = mainNews.kategori?.namaKategori?.uppercase() ?: "UMUM"

        val imgMainUrl = getImageUrl(mainNews.fotoThumbnail)
        Glide.with(this).load(imgMainUrl).centerCrop().into(binding.heroSection.ivHeroMain)

        // -- SUB HERO 1 (Berita 2) --
        if (headlineList.size > 1) {
            val sub1 = headlineList[1]
            binding.heroSection.tvHeroSubTitle1.text = sub1.judulBerita
            binding.heroSection.tvHeroSubCat1.text = sub1.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub1.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub1)
        }

        // -- SUB HERO 2 (Berita 3) --
        if (headlineList.size > 2) {
            val sub2 = headlineList[2]
            binding.heroSection.tvHeroSubTitle2.text = sub2.judulBerita
            binding.heroSection.tvHeroSubCat2.text = sub2.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub2.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub2)
        }

        // -- SUB HERO 3 (Berita 4) --
        if (headlineList.size > 3) {
            val sub3 = headlineList[3]
            binding.heroSection.tvHeroSubTitle3.text = sub3.judulBerita
            binding.heroSection.tvHeroSubCat3.text = sub3.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub3.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub3)
        }
    }

    // Fungsi bantuan buat ngerapihin URL gambar (biar ga usah diketik ulang)
    private fun getImageUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""
        // Kalau URL-nya udah berawalan HTTP, langsung return. Kalau ngga, tambahin domain.
        return if (path.startsWith("http")) path else "https://baru.fenomenatv.com/uploads/thumbnail/$path"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}