package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemKomentarBinding

class KomentarAdapter(private var listKomentar: List<Any> = listOf()) :
    RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    inner class KomentarViewHolder(val binding: ItemKomentarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        // FIX: Parameter kedua harus 'parent' (ViewGroup)
        val binding = ItemKomentarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return KomentarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        val item = listKomentar[position]
        try {
            if (item is Map<*, *>) {
                val user = item["user"] as? Map<*, *>
                holder.binding.tvNamaUser.text = user?.get("username")?.toString() ?: "Anonim"
                holder.binding.tvIsiKomentar.text = item["isi_komentar"]?.toString() ?: ""
            } else {
                // Gunakan refleksi jika model class belum didefinisikan secara statis
                val propertiUser = item.javaClass.getMethod("getUser").invoke(item)
                val username = propertiUser?.javaClass?.getMethod("getUsername")?.invoke(propertiUser)
                val isi = item.javaClass.getMethod("getIsiKomentar").invoke(item)

                holder.binding.tvNamaUser.text = username?.toString() ?: "Anonim"
                holder.binding.tvIsiKomentar.text = isi?.toString() ?: ""
            }
        } catch (e: Exception) {
            holder.binding.tvNamaUser.text = "Pembaca FNM"
            holder.binding.tvIsiKomentar.text = item.toString()
        }
    }

    override fun getItemCount(): Int = listKomentar.size

    fun updateData(newList: List<Any>) {
        this.listKomentar = newList
        notifyDataSetChanged()
    }
}
