package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Redaksi

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R
import java.io.File

class BeritaRedaksiAdapter(
    private var listBerita: List<Berita>,
    private val onItemClick: (Berita) -> Unit
) : RecyclerView.Adapter<BeritaRedaksiAdapter.ViewHolder>() {

    // ViewHolder: Menghubungkan variabel dengan ID di layout item_berita_redaksi.xml
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumbnail: ImageView = view.findViewById(R.id.imgThumbnail)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvInfo: TextView = view.findViewById(R.id.tvInfo)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnVerifikasi: ImageView = view.findViewById(R.id.btnVerifikasi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berita_redaksi, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder: Mengisi data dari model ke komponen UI
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val berita = listBerita[position]
        
        holder.tvJudul.text = berita.judul_berita
        holder.tvInfo.text = "Editor: ${berita.nama_penulis} | Kategori: ${berita.nama_kategori}"
        holder.tvTanggal.text = berita.created_at
        
        // Load Gambar dari Internal Storage
        val imageFile = File(berita.foto_thumbnail)
        if (imageFile.exists()) {
            holder.imgThumbnail.setImageURI(Uri.fromFile(imageFile))
        } else {
            holder.imgThumbnail.setImageResource(android.R.drawable.ic_menu_gallery) // Placeholder jika file tidak ada
        }
        
        // Atur label status sesuai database
        holder.tvStatus.text = berita.status_berita.uppercase()
        when (berita.status_berita) {
            "Pending" -> holder.tvStatus.setBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_orange_dark))
            "Rejected" -> holder.tvStatus.setBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
            "Published" -> holder.tvStatus.setBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_green_dark))
            else -> holder.tvStatus.setBackgroundColor(holder.itemView.context.getColor(android.R.color.darker_gray))
        }

        // Event saat item atau tombol edit diklik
        holder.itemView.setOnClickListener { onItemClick(berita) }
        holder.btnVerifikasi.setOnClickListener { onItemClick(berita) }
    }

    override fun getItemCount(): Int = listBerita.size

    // Fungsi helper untuk memperbarui list data dari fragment
    fun updateData(newList: List<Berita>) {
        listBerita = newList
        notifyDataSetChanged()
    }
}
