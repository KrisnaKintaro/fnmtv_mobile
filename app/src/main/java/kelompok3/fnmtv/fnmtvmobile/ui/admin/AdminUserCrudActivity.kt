package kelompok3.fnmtv.fnmtvmobile.ui.admin

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kelompok3.fnmtv.fnmtvmobile.R
import kelompok3.fnmtv.fnmtvmobile.data.api.ApiClient
import kelompok3.fnmtv.fnmtvmobile.data.model.User
import kelompok3.fnmtv.fnmtvmobile.databinding.ActivityAdminCrudBinding
import kelompok3.fnmtv.fnmtvmobile.ui.adapter.admin.AdminUserAdapter
import kotlinx.coroutines.launch

class AdminUserCrudActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCrudBinding
    private lateinit var adapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCrudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Siapkan Adapter kosong dulu
        adapter = AdminUserAdapter(emptyList(),
            onEdit = { user -> showDialogForm(user) },
            onDelete = { user -> deleteUser(user.id) }
        )
        binding.rvAdminUsers.layoutManager = LinearLayoutManager(this)
        binding.rvAdminUsers.adapter = adapter

        // Tombol Tambah User
        binding.btnAddUser.setOnClickListener {
            showDialogForm(null) // null berarti mode Tambah Baru
        }

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@AdminUserCrudActivity).getAllUsers()
                if (response.isSuccessful) {
                    val users = response.body()?.data ?: emptyList()
                    adapter.updateData(users) // Refresh isi list
                } else {
                    Toast.makeText(this@AdminUserCrudActivity, "Gagal ambil data", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminUserCrudActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialogForm(userToEdit: User?) {
        val isEdit = userToEdit != null
        val view = layoutInflater.inflate(R.layout.dialog_admin_user, null)

        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etRole = view.findViewById<EditText>(R.id.etRole)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)

        // Kalau mode Edit, isi dulu kolomnya
        if (isEdit) {
            etUsername.setText(userToEdit?.username)
            etEmail.setText(userToEdit?.email)
            etRole.setText(userToEdit?.role)
        }

        AlertDialog.Builder(this)
            .setTitle(if (isEdit) "Edit User" else "Tambah User Baru")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val username = etUsername.text.toString()
                val email = etEmail.text.toString()
                val role = etRole.text.toString()
                val password = etPassword.text.toString().takeIf { it.isNotEmpty() }

                val newUser = User(username = username, email = email, role = role, password = password)

                if (isEdit) {
                    saveUser(userToEdit?.id, newUser) // Mode PUT
                } else {
                    saveUser(null, newUser) // Mode POST
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun saveUser(id: Int?, user: User) {
        lifecycleScope.launch {
            try {
                val api = ApiClient.getApiService(this@AdminUserCrudActivity)
                val response = if (id == null) api.addUser(user) else api.updateUser(id, user)

                if (response.isSuccessful) {
                    Toast.makeText(this@AdminUserCrudActivity, "Berhasil simpan user!", Toast.LENGTH_SHORT).show()
                    loadUsers() // Langsung refresh data kalau sukses
                } else {
                    Toast.makeText(this@AdminUserCrudActivity, "Gagal menyimpan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminUserCrudActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteUser(id: Int?) {
        if (id == null) return
        AlertDialog.Builder(this)
            .setMessage("Yakin mau hapus user ini?")
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.getApiService(this@AdminUserCrudActivity).deleteUser(id)
                        if (response.isSuccessful) {
                            Toast.makeText(this@AdminUserCrudActivity, "Terhapus!", Toast.LENGTH_SHORT).show()
                            loadUsers()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@AdminUserCrudActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}