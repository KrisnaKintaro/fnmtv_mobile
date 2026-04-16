package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
// <--- 1. IMPORT KELAS BINDING-NYA --->
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemTopBeritaBinding
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita

class TopBeritaAdapter(
    private val context: Context,
    private val beritas: List<Berita>
) : BaseAdapter() {

    override fun getCount(): Int = beritas.size
    override fun getItem(position: Int): Any = beritas[position]
    override fun getItemId(position: Int): Long = beritas[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // <--- 2. SETUP VIEWBINDING (ViewHolder Pattern) --->
        val binding: ItemTopBeritaBinding
        val view: View

        if (convertView == null) {
            binding = ItemTopBeritaBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemTopBeritaBinding
        }

        val berita = beritas[position]

        binding.txtJudulTop.text = "${position + 1}. ${berita.judul_berita}"
        binding.txtDetailTop.text = berita.nama_penulis // Mengandung rincian View | Komen | Reaksi

        return view
    }
}