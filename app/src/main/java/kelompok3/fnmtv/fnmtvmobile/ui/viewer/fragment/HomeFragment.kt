package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.IklanItem
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentHomeBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.BeritaAdapter
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.TrendingAdapter
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.DetailBeritaActivity
import kotlinx.coroutines.launch

@Suppress("SetTextI18n")
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchDataHome()
        fetchDataIklan()
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

    private fun fetchDataIklan() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(requireContext()).getIklan()
                if (response.isSuccessful && response.body()?.status == "success") {
                    val listIklanHorizontal = response.body()?.data?.horizontalIklan ?: emptyList()
                    val iklanTerpilih = listIklanHorizontal.firstOrNull()

                    if (iklanTerpilih != null) {
                        renderIklan(iklanTerpilih)
                    } else {
                        binding.ivIklanHome.visibility = View.GONE
                        binding.tvPlaceholderIklan.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Log.e("API_IKLAN_ERROR", "Crash saat parsing JSON Iklan: ${e.message}")
            }
        }
    }

    private fun renderIklan(iklan: IklanItem) {
        val imgUrl = if (iklan.gambar?.startsWith("http") == true) {
            iklan.gambar
        } else {
            "https://baru.fenomenatv.com/storage/${iklan.gambar}"
        }

        binding.ivIklanHome.visibility = View.VISIBLE
        binding.tvPlaceholderIklan.visibility = View.GONE

        Glide.with(this)
            .load(imgUrl)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.stat_notify_error)
            .into(binding.ivIklanHome)

        binding.ivIklanHome.setOnClickListener {
            var urlTarget = iklan.linkIklan
            if (!urlTarget.isNullOrEmpty()) {
                if (!urlTarget.startsWith("http://") && !urlTarget.startsWith("https://")) {
                    urlTarget = "https://$urlTarget"
                }
                try {
                    // ✅ OPTIMALISASI: Menggunakan KTX .toUri() sesuai saran Android Studio
                    val intentBrowser = Intent(Intent.ACTION_VIEW, urlTarget.toUri())
                    startActivity(intentBrowser)
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Gagal membuka link iklan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun renderBeritaTerbaru(terbaruList: List<BeritaItem>) {
        if (terbaruList.isEmpty()) return

        val adapter = BeritaAdapter(terbaruList)
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
        if (headlineList.isEmpty()) {
            binding.heroSection.tvHeroMainTitle.text = "Belum Ada Headline Utama"
            binding.heroSection.tvHeroMainCat.text = "INFO"
            return
        }

        val mainNews = headlineList[0]
        binding.heroSection.tvHeroMainTitle.text = mainNews.judulBerita
        binding.heroSection.tvHeroMainCat.text = mainNews.kategori?.namaKategori?.uppercase() ?: "UMUM"

        val imgMainUrl = getImageUrl(mainNews.fotoThumbnail)
        Glide.with(this).load(imgMainUrl).centerCrop().into(binding.heroSection.ivHeroMain)

        binding.heroSection.ivHeroMain.setOnClickListener { bukaDetailBerita(mainNews.slug) }
        binding.heroSection.tvHeroMainTitle.setOnClickListener { bukaDetailBerita(mainNews.slug) }

        if (headlineList.size > 1) {
            val sub1 = headlineList[1]
            binding.heroSection.tvHeroSubTitle1.text = sub1.judulBerita
            binding.heroSection.tvHeroSubCat1.text = sub1.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub1.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub1)
            binding.heroSection.ivHeroSub1.setOnClickListener { bukaDetailBerita(sub1.slug) }
        }

        if (headlineList.size > 2) {
            val sub2 = headlineList[2]
            binding.heroSection.tvHeroSubTitle2.text = sub2.judulBerita
            binding.heroSection.tvHeroSubCat2.text = sub2.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub2.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub2)
            binding.heroSection.ivHeroSub2.setOnClickListener { bukaDetailBerita(sub2.slug) }
        }

        if (headlineList.size > 3) {
            val sub3 = headlineList[3]
            binding.heroSection.tvHeroSubTitle3.text = sub3.judulBerita
            binding.heroSection.tvHeroSubCat3.text = sub3.kategori?.namaKategori?.uppercase() ?: "UMUM"
            Glide.with(this).load(getImageUrl(sub3.fotoThumbnail)).centerCrop().into(binding.heroSection.ivHeroSub3)
            binding.heroSection.ivHeroSub3.setOnClickListener { bukaDetailBerita(sub3.slug) }
        }
    }

    private fun bukaDetailBerita(slug: String?) {
        if (!slug.isNullOrEmpty()) {
            val intent = Intent(requireContext(), DetailBeritaActivity::class.java)
            intent.putExtra("BERITA_SLUG", slug)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Slug berita kosong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""
        return if (path.startsWith("http")) path else "https://baru.fenomenatv.com/uploads/thumbnail/$path"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}