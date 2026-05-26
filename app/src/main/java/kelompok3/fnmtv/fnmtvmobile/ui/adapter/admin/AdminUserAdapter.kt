package kelompok3.fnmtv.fnmtvmobile.ui.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.data.model.User
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemAdminUserBinding

class AdminUserAdapter(
    private var listUser: List<User>,
    private val onEdit: (User) -> Unit,
    private val onDelete: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.ViewHolder>() {

    fun updateData(newList: List<User>) {
        listUser = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemAdminUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvNamaUser.text = user.username
            binding.tvEmailUser.text = user.email
            binding.tvRoleUser.text = user.role?.uppercase()

            binding.btnEdit.setOnClickListener { onEdit(user) }
            binding.btnDelete.setOnClickListener { onDelete(user) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = listUser.size
}