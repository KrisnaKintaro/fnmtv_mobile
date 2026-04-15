package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
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
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_kategori, parent, false)
        val kategori = kategoris[position]

        val txtNama = view.findViewById<TextView>(R.id.txt_nama_kategori)
        val txtInisial = view.findViewById<TextView>(R.id.txt_inisial_kategori)
        val btnOpsi = view.findViewById<ImageButton>(R.id.btn_opsi_kategori)

        txtNama.text = kategori.nama_kategori
        // Ambil huruf pertama buat jadi ikon bulat
        txtInisial.text = if (kategori.nama_kategori.isNotEmpty()) kategori.nama_kategori.take(1).uppercase() else "#"

        btnOpsi.setOnClickListener {
            // Panggil popup dengan Theme Merah FNM dan paksa nempel kanan bawah
            val wrapper = ContextThemeWrapper(context, R.style.RedPopupMenu)
            val popupMenu = PopupMenu(wrapper, btnOpsi, Gravity.END)

            popupMenu.menuInflater.inflate(R.menu.menu_opsi_kategori, popupMenu.menu)

            // Trik nampilin icon di PopupMenu
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

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit_kategori -> { onEditClick(kategori); true }
                    R.id.menu_hapus_kategori -> { onDeleteClick(kategori); true }
                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }
}