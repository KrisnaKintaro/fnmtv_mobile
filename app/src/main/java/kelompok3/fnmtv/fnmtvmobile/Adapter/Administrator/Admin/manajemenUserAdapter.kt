package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User
import kelompok3.fnmtv.fnmtvmobile.R

class manajemenUserAdapter(private val context: Context, private val users: List<User>) : BaseAdapter() {

    override fun getCount(): Int = users.size
    override fun getItem(position: Int): Any = users[position]
    override fun getItemId(position: Int): Long = users[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Panggil layout item_user.xml yang udah kita bikin sebelumnya
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)

        val user = users[position]

        // Hubungkan ke komponen di XML
        val txtNama = view.findViewById<TextView>(R.id.txt_nama_user)
        val txtRole = view.findViewById<TextView>(R.id.txt_role_user)
        val txtInisial = view.findViewById<TextView>(R.id.txt_inisial)

        // Isi Datanya
        txtNama.text = user.username

        // Kita nampilin Role sama Status biar lebih lengkap
        txtRole.text = "Role: ${user.role} | Status: ${user.status}"

        // Bikin inisial dari huruf pertama username
        if (user.username.isNotEmpty()) {
            txtInisial.text = user.username.take(1).uppercase()
        }

        return view
    }
}