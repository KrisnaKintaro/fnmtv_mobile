package kelompok3.fnmtv.fnmtvmobile.ui.viewer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kelompok3.fnmtv.fnmtvmobile.data.api.volley.VolleyManager
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentSearchBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer.BeritaAdapter

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var beritaAdapter: BeritaAdapter
    // Panggil si Manager di sini
    private lateinit var volleyManager: VolleyManager

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

        // Inisialisasi
        volleyManager = VolleyManager(requireContext())
        beritaAdapter = BeritaAdapter(emptyList())

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }

        val keyword = arguments?.getString(ARG_KEYWORD) ?: ""
        if (keyword.isNotEmpty()) {
            binding.tvSearchHeader.text = "Hasil Pencarian untuk: \"$keyword\""
            performSearch(keyword)
        }
    }

    private fun performSearch(keyword: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmptySearch.visibility = View.GONE

        volleyManager.searchBerita(keyword,
            onSuccess = { listBerita ->
                binding.progressBar.visibility = View.GONE

                // Update UI Header Count
                binding.tvSearchCount.text = "Menemukan ${listBerita.size} artikel"

                if (listBerita.isEmpty()) {
                    binding.tvEmptySearch.visibility = View.VISIBLE
                    binding.rvSearchResults.visibility = View.GONE
                } else {
                    binding.tvEmptySearch.visibility = View.GONE
                    binding.rvSearchResults.visibility = View.VISIBLE
                    beritaAdapter.updateData(listBerita)
                }
            },
            onError = { pesanError ->
                binding.progressBar.visibility = View.GONE
                binding.tvEmptySearch.visibility = View.VISIBLE
                Toast.makeText(requireContext(), pesanError, Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}