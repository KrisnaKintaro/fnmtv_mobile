package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Editor.BeritaEditorAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Editor.EditorController
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentListDraftBinding

class ListDraftFragment : Fragment() {

    private var _binding: FragmentListDraftBinding? = null
    private val binding get() = _binding!!
    private lateinit var controller: EditorController
    private lateinit var adapter: BeritaEditorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = EditorController(requireContext())

        setupRecyclerView()
        loadData()

        binding.fabTambahBerita.setOnClickListener {
            val fragment = CreateBeritaFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        adapter = BeritaEditorAdapter(listOf()) { berita ->
            if (berita.status_berita == "Rejected") {
                val fragment = DetailRevisiFragment.newInstance(berita.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            } else {
                val fragment = CreateBeritaFragment.newInstance(berita.id)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        binding.rvBeritaEditor.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeritaEditor.adapter = adapter
    }

    private fun loadData() {
        // Dummy userId = 1, sesuaikan dengan session login jika sudah ada
        val data = controller.getBeritaSaya(1)
        adapter.updateData(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
