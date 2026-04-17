package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin.manajemenUserAdapter
import kelompok3.fnmtv.fnmtvmobile.Controller.Administrator.Admin.UserController
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.databinding.FragmentManajemenUserBinding
import kelompok3.fnmtv.fnmtvmobile.databinding.DialogTambahEditUserBinding
import kelompok3.fnmtv.fnmtvmobile.databinding.DialogKonfirmasiHapusBinding

class manajemenUserFragment : Fragment() {

    private var _binding: FragmentManajemenUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var userController: UserController
    private lateinit var adapter: manajemenUserAdapter
    private var semuaUser: List<User> = listOf()
    private val selectedUsers = mutableSetOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManajemenUserBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userController = UserController(requireContext())
        loadDataDariDatabase()

        // Setup Spinner Role Filter
        val roles = arrayOf("Semua Role", "Admin", "Redaksi", "Editor", "Viewer")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinRoleFilter.adapter = spinnerAdapter

        // Action kalau Spinner dipilih
        binding.spinRoleFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    terapkanFilterGanda()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        // Fitur live search
        binding.acSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                terapkanFilterGanda()
            }
        })
        binding.btnTambahUser.setOnClickListener { tampilkanDialogTambahEdit(null) } // null = Mode Tambah

        registerForContextMenu(binding.lvUsers)
    }

    // Fungsi load dan filter data
    private fun loadDataDariDatabase() {
        semuaUser = userController.getAllUsers()
        val listSugestiNama = semuaUser.map { it.username }.toTypedArray()
        val autoAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            listSugestiNama
        )
        binding.acSearchUser.setAdapter(autoAdapter)

        adapter = manajemenUserAdapter(
            context = requireContext(),
            users = semuaUser,
            onEditClick = { user -> tampilkanDialogTambahEdit(user) },
            onDeleteClick = { user -> tampilkanDialogKonfirmasiHapus(user) },
        )
        binding.lvUsers.adapter = adapter
    }

    // Fitur filter gabungan: Nyari nama + Nyari role sekaligus
    private fun terapkanFilterGanda() {
        val keyword = binding.acSearchUser.text.toString().lowercase()
        val roleTerpilih = binding.spinRoleFilter.selectedItem?.toString() ?: "Semua Role"

        val hasilPencarian = semuaUser.filter { user ->
            val cocokNama = user.username.lowercase().contains(keyword)
            val cocokRole = if (roleTerpilih == "Semua Role") true else user.role == roleTerpilih
            cocokNama && cocokRole
        }
        adapter.updateData(hasilPencarian)
    }

    private fun tampilkanDialogTambahEdit(user: User?) {
        val dialogBinding = DialogTambahEditUserBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val roles = arrayOf("Admin", "Redaksi", "Editor", "Viewer")
        dialogBinding.spinFormRole.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)

        // Isi form otomatis kalau mode Edit
        if (user != null) {
            dialogBinding.tvDialogTitle.text = "Edit Pengguna"
            dialogBinding.etFormUsername.setText(user.username)
            dialogBinding.etFormEmail.setText(user.email)
            // Password sengaja gak di-setText biar kosong, kalau diisi berarti user mau ganti password
            dialogBinding.lblPasswordUser.text = "Password (Kosongi jika tak ingin diubah)"
            if (user.status == "Aktif") dialogBinding.rbAktif.isChecked =
                true else dialogBinding.rbNonaktif.isChecked = true
        } else {
            dialogBinding.tvDialogTitle.text = "Tambah Pengguna Baru"
            dialogBinding.rbAktif.isChecked = true
        }

        dialogBinding.btnBatalUser.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnSimpanUser.setOnClickListener {
            val inputUsername = dialogBinding.etFormUsername.text.toString().trim()
            val inputEmail = dialogBinding.etFormEmail.text.toString().trim()
            val inputPassword =
                dialogBinding.etFormPassword.text.toString().trim() // <--- AMBIL TEKS PASSWORD
            val inputRole = dialogBinding.spinFormRole.selectedItem.toString()
            val inputStatus = if (dialogBinding.rbAktif.isChecked) "Aktif" else "Nonaktif"

            // Validasi Dasar (Username & Email gak boleh kosong)
            if (inputUsername.isEmpty() || inputEmail.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Username dan Email wajib diisi cuy!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (user == null) {
                // Kalau tambah baru, Password wajib diisi
                if (inputPassword.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Password wajib diisi untuk User baru!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val newUser =
                    User(0, inputUsername, inputEmail, inputRole, inputStatus, inputPassword)
                val sukses = userController.tambahUser(newUser)
                if (sukses) {
                    Toast.makeText(
                        requireContext(),
                        "User baru berhasil ditambahkan!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            } else {
                // Kalau password dikosongin, berarti pakai password lama. Kalau diisi, pakai password baru.
                val finalPassword = if (inputPassword.isNotEmpty()) inputPassword else user.password

                val updatedUser =
                    User(user.id, inputUsername, inputEmail, inputRole, inputStatus, finalPassword)
                val sukses = userController.editUser(updatedUser)
                if (sukses) {
                    Toast.makeText(
                        requireContext(),
                        "Data berhasil diperbarui!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun tampilkanDialogKonfirmasiHapus(user: User) {
        val dialogBinding = DialogKonfirmasiHapusBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogBinding.root).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.tvHapusUsername.text = user.username
        dialogBinding.tvHapusRole.text = "Role: ${user.role}"

        dialogBinding.btnBatalHapus.setOnClickListener { dialog.dismiss() }

        dialogBinding.btnKonfirmasiHapus.setOnClickListener {
            if (userController.hapusUser(user.id)) {
                Toast.makeText(requireContext(), "${user.username} musnah!", Toast.LENGTH_SHORT)
                    .show()
                loadDataDariDatabase()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    // Ketika tombol refresh di navbar atas di klik
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            loadDataDariDatabase()
            Toast.makeText(context, "Data berhasil disegarkan!", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}