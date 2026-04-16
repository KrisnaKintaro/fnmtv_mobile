package kelompok3.fnmtv.fnmtvmobile.Adapter.Viewers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kelompok3.fnmtv.fnmtvmobile.Database.Model.Komentar
import kelompok3.fnmtv.fnmtvmobile.R

class KomentarAdapter(
    private val listKomentar: List<Komentar>
) : RecyclerView.Adapter<KomentarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamaUser: TextView = view.findViewById(R.id.tvNamaUser)
        val tvIsiKomentar: TextView = view.findViewById(R.id.tvIsiKomentar)
        val tvWaktuKomentar: TextView = view.findViewById(R.id.tvWaktuKomentar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_komentar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val komentar = listKomentar[position]
        holder.tvNamaUser.text = komentar.nama_user ?: "Anonim"
        holder.tvIsiKomentar.text = komentar.isi_komentar
        holder.tvWaktuKomentar.text = komentar.created_at ?: "-"
    }

    override fun getItemCount(): Int = listKomentar.size
}