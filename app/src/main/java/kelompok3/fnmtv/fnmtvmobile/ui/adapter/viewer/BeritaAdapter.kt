package kelompok3.fnmtv.fnmtvmobile.ui.adapter.viewer

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kelompok3.fnmtv.fnmtvmobile.data.model.viewer.BeritaItem
import kelompok3.fnmtv.fnmtvmobile.databinding.ItemBeritaViewerBinding
import kelompok3.fnmtv.fnmtvmobile.ui.viewer.DetailBeritaActivity

class BeritaAdapter(private var listBerita: List<BeritaItem>) :
    RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    fun updateData(newList: List<BeritaItem>) {
        listBerita = newList
        notifyDataSetChanged()
    }

    inner class BeritaViewHolder(val binding: ItemBeritaViewerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(berita: BeritaItem) {
            binding.tvJudulBerita.text = berita.judulBerita
            binding.tvKategori.text = berita.kategori?.namaKategori?.uppercase() ?: "UMUM"

            val views = "${berita.jumlahView ?: "0"} Views"
            val tanggal = berita.waktuPublikasi?.substringBefore("T") ?: ""
            binding.tvTanggal.text = "$tanggal • $views"

            val imgUrl = if (berita.fotoThumbnail?.startsWith("http") == true) {
                berita.fotoThumbnail
            } else {
                "https://baru.fenomenatv.com/uploads/thumbnail/${berita.fotoThumbnail}"
            }

            Glide.with(itemView.context)
                .load(imgUrl)
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.ivThumbnail)

            // Klik Item pindah ke Detail
            binding.root.setOnClickListener {
                val intent = Intent(itemView.context, DetailBeritaActivity::class.java)
                intent.putExtra("BERITA_SLUG", berita.slug)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val binding = ItemBeritaViewerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BeritaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        holder.bind(listBerita[position])
    }

    override fun getItemCount(): Int = listBerita.size
}
