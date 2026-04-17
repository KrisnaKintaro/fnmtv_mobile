package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Gravity
import android.widget.BaseAdapter
import android.widget.PopupMenu
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemUserBinding
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
        val binding: ItemUserBinding
        val view: View

        if (convertView == null) {
            // Kalau tampilan belum ada, inflate pakai binding
            binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding // Simpan binding di "saku" view-nya
        } else {
            // Kalau tampilan udah ada (bekas scroll), ambil lagi binding dari "saku"
            view = convertView
            binding = view.tag as ItemUserBinding
        }

        val user = users[position]

        binding.txtNamaUser.text = user.username
        binding.txtRoleUser.text = "Role: ${user.role} | Status: ${user.status}"

        // Set Inisial Role (Ambil huruf pertama dari Role)
        binding.txtInisialRole.text =
            if (user.role.isNotEmpty()) user.role.substring(0, 1).uppercase() else "?"

        // Listener buat PopupMenu dengan Theme
        binding.btnOpsiUser.setOnClickListener {
            // Arahkan ke style RedPopupMenu
            val wrapper = ContextThemeWrapper(context, R.style.RedPopupMenu)
            val popupMenu = PopupMenu(wrapper, binding.btnOpsiUser, Gravity.END)

            popupMenu.menuInflater.inflate(R.menu.menu_opsi_user, popupMenu.menu)

            // munculkan Ikon di PopupMenu pakai 'Reflection'
            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popupMenu)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Handle klik itemnya sesuai ID di XML
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