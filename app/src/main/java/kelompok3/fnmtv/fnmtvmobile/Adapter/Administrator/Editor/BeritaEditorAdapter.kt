package kelompok3.fnmtv.fnmtvmobile.Adapter.Administrator.Editor

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Berita
import kelompok3.fnmtv.fnmtvmobile.R

class BeritaEditorAdapter(
    private var listBerita: List<Berita>,
    private val onItemClick: (Berita) -> Unit
) : RecyclerView.Adapter<BeritaEditorAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgThumbnail: ImageView = view.findViewById(R.id.img_thumbnail_editor)
        val txtJudul: TextView = view.findViewById(R.id.txt_judul_berita_editor)
        val txtTanggal: TextView = view.findViewById(R.id.txt_tanggal_berita_editor)
        val txtStatus: TextView = view.findViewById(R.id.txt_status_label_editor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berita_editor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val berita = listBerita[position]
        
        holder.txtJudul.text = berita.judul_berita
        holder.txtTanggal.text = berita.created_at ?: "Baru saja"
        holder.txtStatus.text = berita.status_berita

        val statusColor = when (berita.status_berita) {
            "Draft" -> "#9E9E9E"     // Abu-abu
            "Pending" -> "#FBC02D"   // Kuning
            "Rejected" -> "#D32F2F"  // Merah
            "Published" -> "#388E3C" // Hijau
            else -> "#757575"
        }

        try {
            holder.txtStatus.background.setTint(Color.parseColor(statusColor))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener { onItemClick(berita) }
    }

    override fun getItemCount(): Int = listBerita.size

    fun updateData(newList: List<Berita>) {
        this.listBerita = newList
        notifyDataSetChanged()
    }
}
