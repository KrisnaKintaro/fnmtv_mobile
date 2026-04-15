package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.ContextThemeWrapper // PENTING BUAT THEME POPUP
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.*
import kelompok3.fnmtv.fnmtvmobile.Database.Model.User
import kelompok3.fnmtv.fnmtvmobile.R

class manajemenUserAdapter(
    private val context: Context,
    private var users: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : BaseAdapter() {

    fun updateData(newUsers: List<User>) {
        this.users = newUsers
        notifyDataSetChanged()
    }

    override fun getCount(): Int = users.size
    override fun getItem(position: Int): Any = users[position]
    override fun getItemId(position: Int): Long = users[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        val user = users[position]

        val txtNama = view.findViewById<TextView>(R.id.txt_nama_user)
        val txtRole = view.findViewById<TextView>(R.id.txt_role_user)
        val txtInisialRole = view.findViewById<TextView>(R.id.txt_inisial_role) // Inisial Role
        val btnOpsi = view.findViewById<ImageButton>(R.id.btn_opsi_user)

        txtNama.text = user.username
        txtRole.text = "Role: ${user.role} | Status: ${user.status}"

        // Set Inisial Role (Ambil huruf pertama dari Role)
        txtInisialRole.text = if (user.role.isNotEmpty()) user.role.substring(0, 1).uppercase() else "?"

        // Listener buat PopupMenu dengan Theme
        btnOpsi.setOnClickListener {
            // GANTI BAGIAN INI: Arahkan ke style RedPopupMenu buatan lu
            val wrapper = ContextThemeWrapper(context, R.style.RedPopupMenu)
            val popupMenu = PopupMenu(wrapper, btnOpsi, Gravity.END)

            popupMenu.menuInflater.inflate(R.menu.menu_opsi_user, popupMenu.menu)

            // 2. TRIK KHUSUS: Munculkan Ikon di PopupMenu
            // Ini menggunakan 'Reflection' biar ic_edit & ic_hapus kelihatan
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 3. Handle klik itemnya sesuai ID di XML
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit_user -> {
                        onEditClick(user)
                        true
                    }
                    R.id.menu_hapus_user -> {
                        onDeleteClick(user)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }
}