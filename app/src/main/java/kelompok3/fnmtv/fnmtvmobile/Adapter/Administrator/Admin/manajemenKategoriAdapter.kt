package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.PopupMenu
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemKategoriBinding
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Kategori
import kelompok3.fnmtv.fnmtvmobile.R

class manajemenKategoriAdapter(
    private val context: Context,
    private var kategoris: List<Kategori>,
    private val onEditClick: (Kategori) -> Unit,
    private val onDeleteClick: (Kategori) -> Unit
) : BaseAdapter() {

    fun updateData(newKategoris: List<Kategori>) {
        this.kategoris = newKategoris
        notifyDataSetChanged()
    }

    override fun getCount(): Int = kategoris.size
    override fun getItem(position: Int): Any = kategoris[position]
    override fun getItemId(position: Int): Long = kategoris[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemKategoriBinding
        val view: View

        if (convertView == null) {
            // Kalau tampilan belum ada, inflate pakai binding
            binding = ItemKategoriBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding // Simpan binding di "saku" (tag) view-nya
        } else {
            // Kalau tampilan udah ada (bekas scroll), ambil lagi binding dari "saku"
            view = convertView
            binding = view.tag as ItemKategoriBinding
        }

        val kategori = kategoris[position]

        binding.txtNamaKategori.text = kategori.nama_kategori
        binding.txtInisialKategori.text =
            if (kategori.nama_kategori.isNotEmpty()) kategori.nama_kategori.take(1)
                .uppercase() else "#"

        binding.btnOpsiKategori.setOnClickListener {
            // Panggil popup dengan Theme Merah FNM dan paksa nempel kanan bawah
            val wrapper = ContextThemeWrapper(context, R.style.RedPopupMenu)
            val popupMenu = PopupMenu(wrapper, binding.btnOpsiKategori, Gravity.END)

            popupMenu.menuInflater.inflate(R.menu.menu_opsi_kategori, popupMenu.menu)

            // Trik nampilin icon di PopupMenu
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

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit_kategori -> {
                        onEditClick(kategori); true
                    }

                    R.id.menu_hapus_kategori -> {
                        onDeleteClick(kategori); true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }
}