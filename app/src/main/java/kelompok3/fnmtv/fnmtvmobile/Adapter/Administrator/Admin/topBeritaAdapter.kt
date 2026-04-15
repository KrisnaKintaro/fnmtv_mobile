package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import java.text.NumberFormat
import java.util.*

class TopBeritaAdapter(
    private val context: Context,
    private val beritas: List<Berita>
) : BaseAdapter() {

    override fun getCount(): Int = beritas.size
    override fun getItem(position: Int): Any = beritas[position]
    override fun getItemId(position: Int): Long = beritas[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
        val berita = beritas[position]

        val txtJudul = view.findViewById<TextView>(android.R.id.text1)
        val txtDetail = view.findViewById<TextView>(android.R.id.text2)

        // Baris 1: Judul Berita
        txtJudul.text = "${position + 1}. ${berita.judul_berita}"
        txtJudul.textSize = 14f
        txtJudul.setPadding(0, 8, 0, 4)
        txtJudul.setTextColor(context.resources.getColor(android.R.color.black))

        // Baris 2: Rincian Data Real (View | Komentar | Reaksi)
        // Kita panggil properti nama_penulis yang sudah diisi format string di Controller tadi
        txtDetail.text = berita.nama_penulis
        txtDetail.textSize = 12f
        txtDetail.setPadding(0, 0, 0, 8)
        txtDetail.setTextColor(context.resources.getColor(android.R.color.darker_gray))

        return view
    }
}