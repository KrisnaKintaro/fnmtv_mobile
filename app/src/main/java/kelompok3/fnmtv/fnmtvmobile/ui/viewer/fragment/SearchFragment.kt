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

    private lateinit var adapter: BeritaAdapter
    private lateinit var volleyManager: VolleyManager
    private var keyword: String = ""

    companion object {
        fun newInstance(keyword: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString("KEYWORD", keyword)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        keyword = arguments?.getString("KEYWORD") ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        volleyManager = VolleyManager(requireContext())
        adapter = BeritaAdapter(emptyList()) // Siapin adapter kosong

        binding.rvHasilPencarian.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHasilPencarian.adapter = adapter

        if (keyword.isNotEmpty()) {
            jalankanPencarian(keyword)
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvKosong.visibility = View.VISIBLE
            binding.tvKosong.text = "Ketikkan sesuatu di kolom pencarian."
        }
    }

    private fun jalankanPencarian(keyword: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvHasilPencarian.visibility = View.GONE
        binding.tvKosong.visibility = View.GONE

        // Minta Volley nyari data ke Laravel
        volleyManager.searchBerita(
            keyword = keyword,
            onSuccess = { listHasil ->
                // Pastiin fragment masih kebuka sebelum update UI (mencegah crash)
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    if (listHasil.isNotEmpty()) {
                        adapter.updateData(listHasil)
                        binding.rvHasilPencarian.visibility = View.VISIBLE
                    } else {
                        binding.tvKosong.visibility = View.VISIBLE
                        binding.tvKosong.text = "Hasil pencarian \"$keyword\" tidak ditemukan."
                    }
                }
            },
            onError = { pesanError ->
                if (_binding != null) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvKosong.visibility = View.VISIBLE
                    binding.tvKosong.text = "Gagal memuat: $pesanError"
                    Toast.makeText(requireContext(), pesanError, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}