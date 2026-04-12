package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin.manajemenUserAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin.UserController
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentManajemenUserBinding // Sesuaikan sama nama XML lu

class manajemenUserFragment : Fragment() {

    private var _binding: FragmentManajemenUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManajemenUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Panggil Controller
        val userController = UserController(requireContext())

        // 2. Tarik Data User dari SQLite
        val userList = userController.getAllUsers()

        // 3. Pasang Adapter
        val adapter = manajemenUserAdapter(requireContext(), userList)

        // 4. Set ke ListView
        binding.lvUsers.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Cegah memory leak
        _binding = null
    }
}