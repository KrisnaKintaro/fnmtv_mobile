package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentKategoriBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.BeritaAdapter
import kotlinx.coroutines.launch

class KategoriFragment : Fragment() {
    private var _binding: FragmentKategoriBinding? = null
    private val binding get() = _binding!!
    private var slug: String? = null

    companion object {
        fun newInstance(slug: String, nama: String): KategoriFragment {
            val fragment = KategoriFragment()
            val args = Bundle()
            args.putString("SLUG", slug)
            args.putString("NAMA", nama)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKategoriBinding.inflate(inflater, container, false)
        slug = arguments?.getString("SLUG")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchBeritaKategori()
    }

    private fun fetchBeritaKategori() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Mengambil semua berita lalu filter berdasarkan kategori di sisi client
                val response = ApiClient.getApiService(requireContext()).getSemuaBerita()
                if (response.isSuccessful) {
                    val allNews = response.body()?.data?.terbaru ?: emptyList()
                    val namaKategoriSelected = arguments?.getString("NAMA")?.lowercase()
                    
                    val filteredNews = allNews.filter { 
                        it.kategori?.namaKategori?.lowercase() == namaKategoriSelected 
                    }
                    
                    binding.rvBeritaKategori.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvBeritaKategori.adapter = BeritaAdapter(filteredNews)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
