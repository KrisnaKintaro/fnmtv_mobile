package kelompok3.fnmtv.fnmtvmobile.View.Administrator.Admin

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import java.util.Calendar

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

        // 1. Setup Spinner Role Filter
        val roles = arrayOf("Semua Role", "Admin", "Redaksi", "Editor", "Viewer")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)
        binding.spinRoleFilter.adapter = spinnerAdapter

        // Action kalau Spinner dipilih
        binding.spinRoleFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                terapkanFilterGanda()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // 2. Action Live Search dari AutoCompleteTextView
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

    // --- FUNGSI LOAD & FILTER DATA ---
    private fun loadDataDariDatabase() {
        semuaUser = userController.getAllUsers()
        selectedUsers.clear() // Kosongin cache checkbox tiap refresh

        val listSugestiNama = semuaUser.map { it.username }.toTypedArray()
        val autoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, listSugestiNama)
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

    // --- FUNGSI FORM TAMBAH & EDIT (CRUD UTAMA) ---
    private fun tampilkanDialogTambahEdit(user: User?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_tambah_edit_user, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_dialog_title)
        val etUsername = dialogView.findViewById<EditText>(R.id.et_form_username)
        val etEmail = dialogView.findViewById<EditText>(R.id.et_form_email)
        val lblPassword = dialogView.findViewById<TextView>(R.id.lbl_password_user)
        val etPassword = dialogView.findViewById<EditText>(R.id.et_form_password) // <--- TANGKAP ID PASSWORD
        val spinRole = dialogView.findViewById<Spinner>(R.id.spin_form_role)
        val rbAktif = dialogView.findViewById<RadioButton>(R.id.rb_aktif)
        val rbNonaktif = dialogView.findViewById<RadioButton>(R.id.rb_nonaktif)
        val btnSimpan = dialogView.findViewById<Button>(R.id.btn_simpan_user)
        val btnBatal = dialogView.findViewById<Button>(R.id.btn_batal_user)

        val roles = arrayOf("Admin", "Redaksi", "Editor", "Viewer")
        spinRole.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, roles)

        // Isi form otomatis kalau mode Edit
        if (user != null) {
            tvTitle.text = "Edit Pengguna"
            etUsername.setText(user.username)
            etEmail.setText(user.email)
            // Password sengaja gak di-setText biar kosong, kalau diisi berarti user mau ganti password
            lblPassword.text = "Password (Kosongi jika tak ingin diubah)"
            if (user.status == "Aktif") rbAktif.isChecked = true else rbNonaktif.isChecked = true
        } else {
            tvTitle.text = "Tambah Pengguna Baru"
            rbAktif.isChecked = true
        }

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val inputUsername = etUsername.text.toString().trim()
            val inputEmail = etEmail.text.toString().trim()
            val inputPassword = etPassword.text.toString().trim() // <--- AMBIL TEKS PASSWORD
            val inputRole = spinRole.selectedItem.toString()
            val inputStatus = if (rbAktif.isChecked) "Aktif" else "Nonaktif"

            // Validasi Dasar (Username & Email gak boleh kosong)
            if (inputUsername.isEmpty() || inputEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Username dan Email wajib diisi cuy!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (user == null) {
                // LOGIKA TAMBAH (CREATE)
                // Kalau tambah baru, Password wajib diisi dong!
                if (inputPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Password wajib diisi untuk User baru!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newUser = User(0, inputUsername, inputEmail, inputRole, inputStatus, inputPassword)
                val sukses = userController.tambahUser(newUser)
                if (sukses) {
                    Toast.makeText(requireContext(), "User baru berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            } else {
                // LOGIKA EDIT (UPDATE)
                // Kalau password dikosongin, berarti pakai password lama. Kalau diisi, pakai password baru.
                val finalPassword = if (inputPassword.isNotEmpty()) inputPassword else user.password

                val updatedUser = User(user.id, inputUsername, inputEmail, inputRole, inputStatus, finalPassword)
                val sukses = userController.editUser(updatedUser)
                if (sukses) {
                    Toast.makeText(requireContext(), "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    loadDataDariDatabase()
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    // --- FUNGSI HAPUS ---
    private fun tampilkanDialogKonfirmasiHapus(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_konfirmasi_hapus, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<TextView>(R.id.tv_hapus_username).text = user.username
        dialogView.findViewById<TextView>(R.id.tv_hapus_role).text = "Role: ${user.role}"

        dialogView.findViewById<Button>(R.id.btn_batal_hapus).setOnClickListener { dialog.dismiss() }

        dialogView.findViewById<Button>(R.id.btn_konfirmasi_hapus).setOnClickListener {
            if (userController.hapusUser(user.id)) {
                Toast.makeText(requireContext(), "${user.username} musnah!", Toast.LENGTH_SHORT).show()
                loadDataDariDatabase()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    // --- MENU REFRESH ---
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh) {
            loadDataDariDatabase()
            Toast.makeText(context, "Data berhasil disegarkan!", Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // --- BULK DELETE (CONTEXT MENU KETIKA LIST DIKLIK TAHAN) ---
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.setHeaderTitle("Aksi Massal (${selectedUsers.size} dipilih)")
        menu.add(Menu.NONE, 1, 1, "Hapus Terpilih")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 1) {
            if (selectedUsers.isEmpty()) {
                Toast.makeText(context, "Centang dulu user yang mau dihapus cuy!", Toast.LENGTH_SHORT).show()
                return true
            }
            var suksesHapus = 0
            for (u in selectedUsers) {
                if(userController.hapusUser(u.id)) suksesHapus++
            }
            Toast.makeText(context, "$suksesHapus data berhasil dihapus massal!", Toast.LENGTH_SHORT).show()
            loadDataDariDatabase()
            return true
        }
        return super.onContextItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}