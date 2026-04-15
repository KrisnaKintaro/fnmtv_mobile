package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User
import kelompok3.fnmtv.fnmtvmobile.R
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast

class manajemenUserAdapter(private val context: Context, private val users: List<User>) : BaseAdapter() {

    override fun getCount(): Int = users.size
    override fun getItem(position: Int): Any = users[position]
    override fun getItemId(position: Int): Long = users[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val user = users[position]

        val txtNama = view.findViewById<TextView>(R.id.txt_nama_user)
        val txtRole = view.findViewById<TextView>(R.id.txt_role_user)
        val txtInisial = view.findViewById<TextView>(R.id.txt_inisial)
        val btnOpsi = view.findViewById<Button>(R.id.btn_opsi_user) // Menggunakan Button No. 3

        txtNama.text = user.username
        txtRole.text = "Role: ${user.role} | Status: ${user.status}"

        if (user.username.isNotEmpty()) {
            txtInisial.text = user.username.take(1).uppercase()
        }

        // --- PENERAPAN KOMPONEN NO 12: PopupMenu ---
        btnOpsi.setOnClickListener {
            val popupMenu = PopupMenu(context, btnOpsi)

            // Kita tambahin menu secara manual (bisa juga dari file XML menu)
            popupMenu.menu.add(Menu.NONE, 1, 1, "Edit User")
            popupMenu.menu.add(Menu.NONE, 2, 2, "Hapus User")

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    1 -> {
                        Toast.makeText(context, "Edit ${user.username} diklik", Toast.LENGTH_SHORT).show()
                        // Nanti panggil Form Edit User di sini
                        true
                    }
                    2 -> {
                        Toast.makeText(context, "Hapus ${user.username} diklik", Toast.LENGTH_SHORT).show()
                        // Nanti panggil query DELETE SQLite di sini
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show() // Tampilkan PopupMenu-nya
        }

        return view
    }
}