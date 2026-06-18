package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.KomentarItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemKomentarBinding

class KomentarAdapter(
    private var listKomentar: List<KomentarItem> = listOf(),
    private val currentUserId: Int, // ID user yang lagi login
    private val onEditClicked: (KomentarItem) -> Unit, // Callback buat klik Edit
    private val onDeleteClicked: (KomentarItem) -> Unit  // Callback buat klik Hapus
) : RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

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

        // Nampilin teks nama dan isi komentar
        holder.binding.tvNamaUser.text = item.user?.username ?: "Anonim"
        holder.binding.tvIsiKomentar.text = item.isiKomentar ?: ""

        // CEK KEPEMILIKAN KOMENTAR
        // Kalau userId di komentar sama dengan ID user yang lagi login, munculin tombol Opsi (titik tiga)
        if (item.userId == currentUserId) {
            holder.binding.btnOpsiKomentar.visibility = View.VISIBLE

            // Pasang event klik buat nampilin PopupMenu
            holder.binding.btnOpsiKomentar.setOnClickListener { view ->
                showPopupMenu(view, item)
            }
        } else {
            // Kalau bukan komentar dia, sembunyiin tombolnya
            holder.binding.btnOpsiKomentar.visibility = View.GONE
        }
    }

    private fun showPopupMenu(view: View, komentar: KomentarItem) {
        val popupMenu = PopupMenu(view.context, view)

        // Bikin menu "Edit" dan "Hapus" langsung dari kode (nggak usah bikin XML menu baru)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Edit Komentar")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Hapus Komentar")

        // Nangkep aksi pas menu diklik
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    onEditClicked(komentar) // Jalanin fungsi edit di Activity/Fragment
                    true
                }
                2 -> {
                    onDeleteClicked(komentar) // Jalanin fungsi hapus di Activity/Fragment
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int = listKomentar.size

    fun updateData(newList: List<KomentarItem>) {
        this.listKomentar = newList
        notifyDataSetChanged()
    }
}