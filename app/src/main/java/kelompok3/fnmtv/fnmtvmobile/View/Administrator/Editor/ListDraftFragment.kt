package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Editor

import android.content.Context
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
    private var currentUserId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil userId dari session
        val sharedPref = requireContext().getSharedPreferences("SESSION_FNMTV", Context.MODE_PRIVATE)
        currentUserId = sharedPref.getInt("USER_ID", -1)

        controller = EditorController(requireContext())
        setupRecyclerView()
        loadData()

        binding.fabTambahBerita.setOnClickListener {
            // Buka CreateBeritaFragment mode tulis baru
            navigateToFragment(CreateBeritaFragment.newInstance())
        }
    }

    // Dipanggil setiap kali fragment kembali ke layar
    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = BeritaEditorAdapter(listOf()) { berita ->
            // Semua status diarahkan ke CreateBeritaFragment
            navigateToFragment(CreateBeritaFragment.newInstance(berita.id))
        }
        binding.rvBeritaEditor.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBeritaEditor.adapter = adapter
    }

    private fun loadData() {
        if (currentUserId == -1) return
        val data = controller.getBeritaSaya(currentUserId)
        adapter.updateData(data)
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}