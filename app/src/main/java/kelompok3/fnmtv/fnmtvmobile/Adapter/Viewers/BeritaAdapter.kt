package kelompok3.fnmtv.fnmtvmobile.Adapter.Viewers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R

class BeritaAdapter(
    private val listBerita: List<Berita>,
    private val onClick: (Berita) -> Unit
) : RecyclerView.Adapter<BeritaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumbnail: ImageView = view.findViewById(R.id.ivThumbnail)
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val tvJudul: TextView = view.findViewById(R.id.tvJudul)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berita_viewer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val berita = listBerita[position]
        holder.tvJudul.text = berita.judul_berita
        holder.tvKategori.text = berita.nama_kategori ?: "Umum"
        holder.tvWaktu.text = berita.waktu_publikasi ?: "-"
        
        // Load image using Glide/Picasso normally, placeholder for now
        
        holder.itemView.setOnClickListener { onClick(berita) }
    }

    override fun getItemCount(): Int = listBerita.size
}