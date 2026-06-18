package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KomentarItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemKomentarBinding

class KomentarAdapter(private var listKomentar: List<KomentarItem> = listOf()) :
    RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    inner class KomentarViewHolder(val binding: ItemKomentarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        val binding = ItemKomentarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return KomentarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        val item = listKomentar[position]
        // Langsung akses property, tidak perlu reflection atau cast Map
        holder.binding.tvNamaUser.text = item.user?.username ?: "Anonim"
        holder.binding.tvIsiKomentar.text = item.isiKomentar ?: ""
    }

    override fun getItemCount(): Int = listKomentar.size

    fun updateData(newList: List<KomentarItem>) {
        this.listKomentar = newList
        notifyDataSetChanged()
    }
}