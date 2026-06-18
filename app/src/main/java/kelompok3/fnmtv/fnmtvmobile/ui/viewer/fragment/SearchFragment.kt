package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KategoriBerita
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.UserBerita
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentSearchBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.BeritaAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var beritaAdapter: BeritaAdapter

    companion object {
        private const val ARG_KEYWORD = "keyword"

        fun newInstance(keyword: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString(ARG_KEYWORD, keyword)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView - ID disesuaikan menjadi rvSearchResults
        beritaAdapter = BeritaAdapter(emptyList())
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }

        // Ambil keyword dari argument lalu jalankan pencarian
        val keyword = arguments?.getString(ARG_KEYWORD) ?: ""
        if (keyword.isNotEmpty()) {
            binding.tvSearchHeader.text = "Hasil Pencarian untuk: \"$keyword\""
            cariBeritaDenganVolley(keyword)
        }
    }

    private fun cariBeritaDenganVolley(keyword: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmptySearch.visibility = View.GONE

        try {
            val encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8")
            val url = "https://baru.fenomenatv.com/api/viewers/search?q=$encodedKeyword"

            val requestQueue = Volley.newRequestQueue(requireContext())

            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    Log.d("SEARCH_RESPONSE", response.toString())
                    try {
                        val status = response.getString("status")
                        if (status == "success") {
                            val hasilBerita = mutableListOf<BeritaItem>()

                            val paginationObject = response.getJSONObject("data")
                            val dataArray = paginationObject.getJSONArray("data")

                            // Update jumlah artikel yang ditemukan di header
                            val totalArtikel = paginationObject.optInt("total", dataArray.length())
                            binding.tvSearchCount.text = "Menemukan $totalArtikel artikel"

                            for (i in 0 until dataArray.length()) {
                                val obj = dataArray.getJSONObject(i)

                                val kategori = if (obj.has("kategori") && !obj.isNull("kategori")) {
                                    val kat = obj.getJSONObject("kategori")
                                    KategoriBerita(namaKategori = kat.optString("nama_kategori", "UMUM"))
                                } else null

                                val user = if (obj.has("user") && !obj.isNull("user")) {
                                    val usr = obj.getJSONObject("user")
                                    UserBerita(username = usr.optString("username", ""))
                                } else null

                                val berita = BeritaItem(
                                    id             = obj.optInt("id"),
                                    judulBerita    = obj.optString("judul_berita"),
                                    slug           = obj.optString("slug"),
                                    fotoThumbnail  = obj.optString("foto_thumbnail"),
                                    waktuPublikasi = obj.optString("waktu_klik", obj.optString("waktu_publikasi")),
                                    jumlahView     = obj.optString("jumlah_view"),
                                    isiBerita      = obj.optString("isi_berita"),
                                    kategori       = kategori,
                                    user           = user
                                )
                                hasilBerita.add(berita)
                            }

                            if (hasilBerita.isEmpty()) {
                                binding.tvEmptySearch.visibility = View.VISIBLE
                                binding.rvSearchResults.visibility = View.GONE
                            } else {
                                binding.tvEmptySearch.visibility = View.GONE
                                binding.rvSearchResults.visibility = View.VISIBLE
                                beritaAdapter.updateData(hasilBerita)
                            }
                        } else {
                            binding.tvEmptySearch.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        Log.e("SEARCH_ERROR", "Parse error: ${e.message}")
                        binding.tvEmptySearch.visibility = View.VISIBLE
                    } finally {
                        binding.progressBar.visibility = View.GONE
                    }
                },
                { error ->
                    Log.e("SEARCH_ERROR", "Volley error: ${error.message}")
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmptySearch.visibility = View.VISIBLE
                }
            )

            requestQueue.add(jsonObjectRequest)

        } catch (e: Exception) {
            Log.e("SEARCH_ERROR", "Encoding error: ${e.message}")
            binding.progressBar.visibility = View.GONE
            binding.tvEmptySearch.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}